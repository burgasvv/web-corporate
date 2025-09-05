package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.EmployeeNotFoundException;
import org.burgas.corporateservice.exception.EmployeeOfficeMatchesException;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.filter.EmployeeFilterFunction;
import org.burgas.corporateservice.service.EmployeeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class EmployeeRouter {

    private final EmployeeServiceImpl employeeService;
    private final EmployeeFilterFunction employeeFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> employeeRoutes() {
        return RouterFunctions.route()
                .filter(this.employeeFilterFunction)
                .GET(
                        "/api/v1/employees/by-corporation", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.employeeService.findByCorporationId(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .GET(
                        "/api/v1/employees/by-office", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.employeeService.findByOffice(request.body(OfficePK.class)))
                )
                .GET(
                        "/api/v1/employees/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.employeeService.findById(
                                                        UUID.fromString(request.param("employeeId").orElseThrow())
                                                )
                                        )
                )
                .POST(
                        "/api/v1/employees/create", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.employeeService.createOrUpdate(
                                                        (EmployeeRequest) request.attribute("employeeRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/employees/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.employeeService.createOrUpdate(
                                                        (EmployeeRequest) request.attribute("employeeRequest").orElseThrow()
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/employees/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.employeeService.delete(
                                                        UUID.fromString(request.param("employeeId").orElseThrow())
                                                )
                                        )
                )
                .PATCH(
                        "/api/v1/employees/office-transfer", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.employeeService.transferToAnotherOffice(
                                                        UUID.fromString(request.param("employeeId").orElseThrow()),
                                                        (OfficePK) request.attribute("officePK").orElseThrow()
                                                )
                                        )
                )
                .onError(
                        DataIntegrityViolationException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        EmployeeNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        EntityFieldEmptyException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        EmployeeOfficeMatchesException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
