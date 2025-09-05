package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.EmptyDirectorIdException;
import org.burgas.corporateservice.exception.MediaNotFoundException;
import org.burgas.corporateservice.exception.WrongDirectorIdException;
import org.burgas.corporateservice.filter.CorporationFilterFunction;
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
@RequiredArgsConstructor
public class CorporationRouter {

    private final CorporationService corporationService;
    private final CorporationFilterFunction corporationFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> corporationRoutes() {
        return RouterFunctions.route()
                .filter(this.corporationFilterFunction)
                .GET(
                        "/api/v1/corporations", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.corporationService.findAll())
                )
                .GET(
                        "/api/v1/corporations/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.corporationService.findById(
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
                                                this.corporationService.createOrUpdate(
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
                                                this.corporationService.createOrUpdate(
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
                                                this.corporationService.delete(
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
                                                this.corporationService.addDirector(
                                                        UUID.fromString(request.param("corporationId").orElseThrow()),
                                                        UUID.fromString(request.param("alreadyDirectorId").orElseThrow()),
                                                        UUID.fromString(request.param("newDirectorId").orElseThrow())
                                                )
                                        )
                )
                .POST(
                        "/api/v1/corporations/upload-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.corporationService.uploadImage(
                                                        UUID.fromString(request.param("corporationId").orElseThrow()),
                                                        request.multipartData().getFirst("file")
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/corporations/change-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.corporationService.changeImage(
                                                        UUID.fromString(request.param("corporationId").orElseThrow()),
                                                        request.multipartData().getFirst("file")
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/corporations/delete-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.corporationService.deleteImage(
                                                        UUID.fromString(request.param("corporationId").orElseThrow())
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
                .onError(
                        MediaNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
