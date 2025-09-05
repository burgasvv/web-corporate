package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.identity.IdentityRequest;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.exception.IdentityNotAuthenticatedException;
import org.burgas.corporateservice.exception.IdentityNotAuthorizedException;
import org.burgas.corporateservice.message.IdentityMessages;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/identities/by-id") ||
                request.path().equals("/api/v1/identities/delete") ||
                request.path().equals("/api/v1/identities/change-password") ||
                request.path().equals("/api/v1/identities/upload-image") ||
                request.path().equals("/api/v1/identities/change-image") ||
                request.path().equals("/api/v1/identities/delete-image") ||
                request.path().equals("/api/v1/identities/make-employee") ||
                request.path().equals("/api/v1/identities/make-director") ||
                request.path().equals("/api/v1/identities/make-user")
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

        } else if (
                request.path().equals("/api/v1/identities/update")
        ) {
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

        }

        return next.handle(request);
    }
}
