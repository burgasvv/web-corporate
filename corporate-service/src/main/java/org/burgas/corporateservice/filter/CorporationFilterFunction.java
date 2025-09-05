package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.corporation.CorporationRequest;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.exception.IdentityNotAuthenticatedException;
import org.burgas.corporateservice.exception.IdentityNotAuthorizedException;
import org.burgas.corporateservice.exception.IdentityNotDirectorException;
import org.burgas.corporateservice.message.IdentityMessages;
import org.burgas.corporateservice.service.CorporationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.*;

import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.IDENTITY_NOT_DIRECTOR;

@Component
@RequiredArgsConstructor
public class CorporationFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CorporationService corporationService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {

        if (
                request.path().equals("/api/v1/corporations/add-director")
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
                request.path().equals("/api/v1/corporations/update")
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
                request.path().equals("/api/v1/corporations/delete") ||
                request.path().equals("/api/v1/corporations/upload-image") ||
                request.path().equals("/api/v1/corporations/change-image") ||
                request.path().equals("/api/v1/corporations/delete-image")
        ) {

            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                Identity identity = (Identity) authentication.getPrincipal();
                UUID corporationId = request.param("corporationId")
                        .map(UUID::fromString)
                        .orElseThrow();
                Corporation corporation = this.corporationService.findCorporation(corporationId);

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
