package org.burgas.corporateservice.router;

import org.burgas.corporateservice.dto.identity.IdentityRequest;
import org.burgas.corporateservice.exception.*;
import org.burgas.corporateservice.filter.IdentityFilterFunction;
import org.burgas.corporateservice.repository.CorporationRepository;
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
public class IdentityRouter {

    @Bean
    public RouterFunction<ServerResponse> identityRoutes(final IdentityService identityService, final CorporationRepository corporationRepository) {
        return RouterFunctions.route()
                .filter(new IdentityFilterFunction(corporationRepository))
                .GET(
                        "/api/v1/identities", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(identityService.findAll())
                )
                .GET(
                        "/api/v1/identities/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(identityService.findAllAsync().get())
                )
                .GET(
                        "/api/v1/identities/by-id", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService.findById(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .GET(
                        "/api/v1/identities/by-id/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService
                                                        .findByIdAsync(UUID.fromString(request.param("identityId").orElseThrow()))
                                                        .get()
                                        )
                )
                .POST(
                        "/api/v1/identities/create", request ->
                                ServerResponse
                                        .status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(identityService.createOrUpdate(request.body(IdentityRequest.class)))
                )
                .POST(
                        "/api/v1/identities/create/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(identityService.createOrUpdateAsync(request.body(IdentityRequest.class)).get())
                )
                .PUT(
                        "/api/v1/identities/update", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService.createOrUpdate(
                                                        (IdentityRequest) request.attribute("identityRequest").orElseThrow()
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/update/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService.createOrUpdateAsync(
                                                        (IdentityRequest) request.attribute("identityRequest").orElseThrow()
                                                ).get()
                                        )
                )
                .DELETE(
                        "/api/v1/identities/delete", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.delete(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/identities/delete/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService
                                                        .deleteAsync(UUID.fromString(request.param("identityId").orElseThrow()))
                                                        .get()
                                        )
                )
                .PATCH(
                        "/api/v1/identities/change-password", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.changePassword(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        request.body(new ParameterizedTypeReference<>() {
                                                        })
                                                )
                                        )
                )
                .PATCH(
                        "/api/v1/identities/change-password/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.changePasswordAsync(
                                                                UUID.fromString(request.param("identityId").orElseThrow()),
                                                                request.body(new ParameterizedTypeReference<>() {
                                                                })
                                                        )
                                                        .get()
                                        )
                )
                .PATCH(
                        "/api/v1/identities/enable-disable", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.enableDisable(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        Boolean.parseBoolean(request.param("enable").orElseThrow())
                                                )
                                        )
                )
                .PATCH(
                        "/api/v1/identities/enable-disable/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.enableDisableAsync(
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
                                                identityService.makeEmployee(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/make-employee/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService.makeEmployeeAsync(
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
                                                identityService.makeDirector(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/make-director/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService.makeDirectorAsync(
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
                                                identityService.makeUser(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/make-user/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(
                                                identityService.makeUserAsync(
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
                                                identityService.uploadImage(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        request.multipartData().getFirst("file")
                                                )
                                        )
                )
                .POST(
                        "/api/v1/identities/upload-image/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.uploadImageAsync(
                                                                UUID.fromString(request.param("identityId").orElseThrow()),
                                                                request.multipartData().getFirst("file")
                                                        )
                                                        .get()
                                        )
                )
                .PUT(
                        "/api/v1/identities/change-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.changeImage(
                                                        UUID.fromString(request.param("identityId").orElseThrow()),
                                                        request.multipartData().getFirst("file")
                                                )
                                        )
                )
                .PUT(
                        "/api/v1/identities/change-image/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.changeImageAsync(
                                                                UUID.fromString(request.param("identityId").orElseThrow()),
                                                                request.multipartData().getFirst("file")
                                                        )
                                                        .get()
                                        )
                )
                .DELETE(
                        "/api/v1/identities/delete-image", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.deleteImage(
                                                        UUID.fromString(request.param("identityId").orElseThrow())
                                                )
                                        )
                )
                .DELETE(
                        "/api/v1/identities/delete-image/async", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                                        .body(
                                                identityService.deleteImageAsync(
                                                                UUID.fromString(request.param("identityId").orElseThrow())
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
