package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.position.PositionRequest;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.exception.PositionNotFoundException;
import org.burgas.corporateservice.filter.PositionFilterFunction;
import org.burgas.corporateservice.service.PositionServiceImpl;
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
public class PositionRouter {

    private final PositionServiceImpl positionService;
    private final PositionFilterFunction positionFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> positionRoutes() {
        return RouterFunctions.route()
                .filter(this.positionFilterFunction)
                .GET(
                        "/api/v1/positions/by-department", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.positionService.findByDepartmentId(
                                                        UUID.fromString(
                                                                request.param("departmentId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .GET(
                        "/api/v1/positions/by-corporation", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.positionService.findByCorporationId(
                                                        UUID.fromString(
                                                                request.param("corporationId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .GET(
                        "/api/v1/positions/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.positionService.findById(
                                                        UUID.fromString(
                                                                request.param("positionId").orElseThrow()
                                                        )
                                                )
                                        )
                )
                .POST(
                        "/api/v1/positions/create", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.positionService.createOrUpdate(
                                                        (PositionRequest) request.attribute("positionRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/positions/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.positionService.createOrUpdate(
                                                        (PositionRequest) request.attribute("positionRequest").orElseThrow()
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/positions/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.positionService.delete(
                                                        UUID.fromString(
                                                                request.param("positionId").orElseThrow()
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
                        PositionNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.OK)
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
                .build();
    }
}
