package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.department.DepartmentRequest;
import org.burgas.corporateservice.exception.DepartmentNotFoundException;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.filter.DepartmentFilterFunction;
import org.burgas.corporateservice.service.DepartmentServiceImpl;
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
public class DepartmentRouter {

    private final DepartmentServiceImpl departmentService;
    private final DepartmentFilterFunction departmentFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> departmentRoutes() {
        return RouterFunctions.route()
                .filter(this.departmentFilterFunction)
                .GET(
                        "/api/v1/departments/by-corporation", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.departmentService.findByCorporation(
                                                        UUID.fromString(
                                                                request.param("corporationId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .GET(
                        "/api/v1/departments/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.departmentService.findById(
                                                        UUID.fromString(
                                                                request.param("departmentId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .POST(
                        "/api/v1/departments/create", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.departmentService.createOrUpdate(
                                                        (DepartmentRequest) request.attribute("departmentRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/departments/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.departmentService.createOrUpdate(
                                                        (DepartmentRequest) request.attribute("departmentRequest").orElseThrow()
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/departments/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.departmentService.delete(
                                                        UUID.fromString(
                                                                request.param("departmentId").orElseThrow()
                                                        )
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
                        EntityFieldEmptyException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        DepartmentNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
