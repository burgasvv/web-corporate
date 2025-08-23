package org.burgas.corporateservice.router;

import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.EmptyDirectorIdException;
import org.burgas.corporateservice.exception.WrongDirectorIdException;
import org.burgas.corporateservice.filter.IdentityFilterFunction;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.burgas.corporateservice.service.CorporationService;
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
public class CorporationRouter {

    @Bean
    public RouterFunction<ServerResponse> corporationRoutes(final CorporationService corporationService, final CorporationRepository corporationRepository) {
        return RouterFunctions.route()
                .filter(new IdentityFilterFunction(corporationRepository))
                .GET(
                        "/api/v1/corporations", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(corporationService.findAll())
                )
                .GET(
                        "/api/v1/corporations/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(corporationService.findAllAsync().get())
                )
                .GET(
                        "/api/v1/corporations/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.findById(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .GET(
                        "/api/v1/corporations/by-id/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.findByIdAsync(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .POST(
                        "/api/v1/corporations/create", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.createOrUpdate(
                                                        request.body(CorporationRequest.class)
                                                )
                                        )
                )
                .POST(
                        "/api/v1/corporations/create/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.createOrUpdateAsync(
                                                        request.body(CorporationRequest.class)
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/corporations/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.createOrUpdate(
                                                        (CorporationRequest) request.attribute("corporationRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/corporations/update/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.createOrUpdateAsync(
                                                        (CorporationRequest) request.attribute("corporationRequest").orElseThrow()
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/corporations/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                corporationService.delete(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/corporations/delete/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                corporationService.deleteAsync(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/corporations/add-director", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                corporationService.addDirector(
                                                        UUID.fromString(request.param("corporationId").orElseThrow()),
                                                        UUID.fromString(request.param("alreadyDirectorId").orElseThrow()),
                                                        UUID.fromString(request.param("newDirectorId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/corporations/add-director/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                corporationService.addDirectorAsync(
                                                        UUID.fromString(request.param("corporationId").orElseThrow()),
                                                        UUID.fromString(request.param("alreadyDirectorId").orElseThrow()),
                                                        UUID.fromString(request.param("newDirectorId").orElseThrow())
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
                        CorporationNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        WrongDirectorIdException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        EmptyDirectorIdException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
