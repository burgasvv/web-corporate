package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.dto.identity.IdentityRequest;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.IdentityNotAuthenticatedException;
import org.burgas.corporateservice.exception.IdentityNotAuthorizedException;
import org.burgas.corporateservice.exception.IdentityNotDirectorException;
import org.burgas.corporateservice.message.IdentityMessages;
import org.burgas.corporateservice.repository.CorporationRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.CORPORATION_NOT_FOUND;
import static org.burgas.corporateservice.message.CorporationMessages.IDENTITY_NOT_DIRECTOR;

@Component
@RequiredArgsConstructor
public class IdentityFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CorporationRepository corporationRepository;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/identities/by-id") || request.path().equals("/api/v1/identities/by-id/async") ||
                request.path().equals("/api/v1/identities/delete") || request.path().equals("/api/v1/identities/delete/async") ||
                request.path().equals("/api/v1/identities/change-password") || request.path().equals("/api/v1/identities/change-password/async") ||
                request.path().equals("/api/v1/identities/upload-image") || request.path().equals("/api/v1/identities/upload-image/async") ||
                request.path().equals("/api/v1/identities/change-image") || request.path().equals("/api/v1/identities/change-image/async") ||
                request.path().equals("/api/v1/identities/delete-image") || request.path().equals("/api/v1/identities/delete-image/async") ||
                request.path().equals("/api/v1/identities/make-employee") || request.path().equals("/api/v1/identities/make-employee/async") ||
                request.path().equals("/api/v1/identities/make-director") || request.path().equals("/api/v1/identities/make-director/async") ||
                request.path().equals("/api/v1/identities/make-user") || request.path().equals("/api/v1/identities/make-user/async")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                UUID identityId = request.param("identityId")
                        .map(UUID::fromString)
                        .orElseThrow();
                Identity identity = (Identity) authentication.getPrincipal();

                if (identity.getId().equals(identityId)) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (request.path().equals("/api/v1/identities/update") || request.path().equals("/api/v1/identities/update/async")) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                IdentityRequest identityRequest = request.body(IdentityRequest.class);
                UUID identityId = identityRequest.getId();
                Identity identity = (Identity) authentication.getPrincipal();

                if (identity.getId().equals(identityId)) {
                    request.attributes().put("identityRequest", identityRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/corporations/add-director") || request.path().equals("/api/v1/corporations/add-director/async")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                UUID identityId = request.param("alreadyDirectorId")
                        .map(UUID::fromString)
                        .orElseThrow();
                Identity identity = (Identity) authentication.getPrincipal();

                if (identity.getId().equals(identityId)) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/corporations/update") || request.path().equals("/api/v1/corporations/update/async")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                CorporationRequest corporationRequest = request.body(CorporationRequest.class);
                UUID identityId = corporationRequest.getDirectorId();
                Identity identity = (Identity) authentication.getPrincipal();

                if (identity.getId().equals(identityId)) {
                    request.attributes().put("corporationRequest", corporationRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/corporations/delete") || request.path().equals("/api/v1/corporations/delete/async") ||
                request.path().equals("/api/v1/corporations/upload-image") || request.path().equals("/api/v1/corporations/upload-image/async") ||
                request.path().equals("/api/v1/corporations/change-image") || request.path().equals("/api/v1/corporations/change-image/async") ||
                request.path().equals("/api/v1/corporations/delete-image") || request.path().equals("/api/v1/corporations/delete-image/async")
        ) {

            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                Identity identity = (Identity) authentication.getPrincipal();
                UUID corporationId = request.param("corporationId")
                        .map(UUID::fromString)
                        .orElseThrow();
                Corporation corporation = this.corporationRepository.findById(corporationId)
                        .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));

                if (corporation.getDirectors().contains(identity.getId())) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }
        }

        return next.handle(request);
    }
}
