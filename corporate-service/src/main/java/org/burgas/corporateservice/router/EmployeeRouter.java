package org.burgas.corporateservice.router;

import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.EmployeeNotFoundException;
import org.burgas.corporateservice.exception.EmployeeOfficeMatchesException;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.filter.EmployeeFilterFunction;
import org.burgas.corporateservice.repository.CorporationRepository;
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
public class EmployeeRouter {

    @Bean
    public RouterFunction<ServerResponse> employeeRoutes(final EmployeeServiceImpl employeeService, final CorporationRepository corporationRepository) {
        return RouterFunctions.route()
                .filter(new EmployeeFilterFunction(corporationRepository))
                .GET(
                        "/api/v1/employees/by-corporation", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.findByCorporationId(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .GET(
                        "/api/v1/employees/by-corporation/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.findByCorporationIdAsync(
                                                                UUID.fromString(request.param("corporationId").orElseThrow())
                                                        )
                                                        .get()
                                        )
                )
                .GET(
                        "/api/v1/employees/by-office", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(employeeService.findByOffice(request.body(OfficePK.class)))
                )
                .GET(
                        "/api/v1/employees/by-office/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(employeeService.findByOfficeAsync(request.body(OfficePK.class)).get())
                )
                .GET(
                        "/api/v1/employees/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.findById(
                                                        UUID.fromString(request.param("employeeId").orElseThrow())
                                                )
                                        )
                )
                .GET(
                        "/api/v1/employees/by-id/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.findByIdAsync(
                                                                UUID.fromString(request.param("employeeId").orElseThrow())
                                                        )
                                                        .get()
                                        )
                )
                .POST(
                        "/api/v1/employees/create", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.createOrUpdate(
                                                        (EmployeeRequest) request.attribute("employeeRequest").orElseThrow()
                                                )
                                        )
                )
                .POST(
                        "/api/v1/employees/create/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.createOrUpdateAsync(
                                                                (EmployeeRequest) request.attribute("employeeRequest").orElseThrow()
                                                        )
                                                        .get()
                                        )
                )
                .PUT(
                        "/api/v1/employees/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.createOrUpdate(
                                                        (EmployeeRequest) request.attribute("employeeRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/employees/update/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                employeeService.createOrUpdateAsync(
                                                                (EmployeeRequest) request.attribute("employeeRequest").orElseThrow()
                                                        )
                                                        .get()
                                        )
                )
                .DELETE(
                        "/api/v1/employees/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                employeeService.delete(
                                                        UUID.fromString(request.param("employeeId").orElseThrow())
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/employees/delete/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                employeeService.deleteAsync(
                                                                UUID.fromString(request.param("employeeId").orElseThrow())
                                                        )
                                                        .get()
                                        )
                )
                .PATCH(
                        "/api/v1/employees/office-transfer", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                employeeService.transferToAnotherOffice(
                                                        UUID.fromString(request.param("employeeId").orElseThrow()),
                                                        (OfficePK) request.attribute("officePK").orElseThrow()
                                                )
                                        )
                )
                .PATCH(
                        "/api/v1/employees/office-transfer/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                employeeService.transferToAnotherOfficeAsync(
                                                                UUID.fromString(request.param("employeeId").orElseThrow()),
                                                                (OfficePK) request.attribute("officePK").orElseThrow()
                                                        )
                                                        .get()
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
