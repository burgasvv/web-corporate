package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.office.OfficeRequest;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.EntityFieldEmptyException;
import org.burgas.corporateservice.exception.OfficeNotFoundException;
import org.burgas.corporateservice.filter.OfficeFilterFunction;
import org.burgas.corporateservice.service.OfficeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class OfficeRouter {

    private final OfficeServiceImpl officeService;
    private final OfficeFilterFunction officeFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> officeRoutes() {
        return RouterFunctions.route()
                .filter(this.officeFilterFunction)
                .GET(
                        "/api/v1/offices/by-corporation", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.officeService.findByCorporationId(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .GET(
                        "/api/v1/offices/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.officeService.findById(request.body(OfficePK.class)))
                )
                .POST(
                        "/api/v1/offices/create", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.officeService.createOrUpdate(
                                                        (OfficeRequest) request.attribute("officeRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/offices/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.officeService.createOrUpdate(
                                                        (OfficeRequest) request.attribute("officeRequest").orElseThrow()
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/offices/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.officeService.delete(
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
                        OfficeNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_FOUND)
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
