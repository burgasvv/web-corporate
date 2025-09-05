package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.identity.IdentityRequest;
import org.burgas.corporateservice.exception.*;
import org.burgas.corporateservice.filter.IdentityFilterFunction;
import org.burgas.corporateservice.service.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
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
public class IdentityRouter {

    private final IdentityService identityService;
    private final IdentityFilterFunction identityFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> identityRoutes() {
        return RouterFunctions.route()
                .filter(this.identityFilterFunction)
                .GET(
                        "/api/v1/identities", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.identityService.findAll())
                )
                .GET(
                        "/api/v1/identities/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.identityService.findById(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .POST(
                        "/api/v1/identities/create", request ->
                                ServerResponse
                                        .status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(this.identityService.createOrUpdate(request.body(IdentityRequest.class)))
                )
                .PUT(
                        "/api/v1/identities/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.identityService.createOrUpdate(
                                                        (IdentityRequest) request.attribute("identityRequest").orElseThrow()
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/identities/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.identityService.delete(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .PATCH(
                        "/api/v1/identities/change-password", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.identityService.changePassword(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        request.body(new ParameterizedTypeReference<>() {
                                                        })
                                                )
                                        )
                )
                .PATCH(
                        "/api/v1/identities/enable-disable", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.identityService.enableDisable(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        Boolean.parseBoolean(request.param("enable").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/make-employee", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.identityService.makeEmployee(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/make-director", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.identityService.makeDirector(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/make-user", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                this.identityService.makeUser(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .POST(
                        "/api/v1/identities/upload-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.identityService.uploadImage(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        request.multipartData().getFirst("file")
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/change-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.identityService.changeImage(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        request.multipartData().getFirst("file")
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/identities/delete-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                this.identityService.deleteImage(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
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
                        IdentityNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        MediaNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        PasswordMatchesException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        PasswordNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        EnableDisableMatchesException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        PartFileEmptyException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
