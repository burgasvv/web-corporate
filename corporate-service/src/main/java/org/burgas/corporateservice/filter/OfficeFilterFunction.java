package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.office.OfficeRequest;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.IdentityNotAuthenticatedException;
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.CORPORATION_NOT_FOUND;
import static org.burgas.corporateservice.message.CorporationMessages.IDENTITY_NOT_DIRECTOR;

@Component
@RequiredArgsConstructor
public class OfficeFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CorporationRepository corporationRepository;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/offices/create") ||
                request.path().equals("/api/v1/offices/update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                OfficeRequest officeRequest = request.body(OfficeRequest.class);
                UUID corporationId = officeRequest.getOffice().getCorporationId();
                Corporation corporation = getCorporation(corporationId);
                Identity identity = (Identity) authentication.getPrincipal();

                if (corporation.getDirectors().contains(identity.getId())) {
                    request.attributes().put("officeRequest", officeRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (request.path().equals("/api/v1/offices/delete")) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                OfficePK officePK = request.body(OfficePK.class);
                UUID corporationId = officePK.getCorporationId();
                Corporation corporation = getCorporation(corporationId);
                Identity identity = (Identity) authentication.getPrincipal();

                if (corporation.getDirectors().contains(identity.getId())) {
                    request.attributes().put("officePK", officePK);
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

    private Corporation getCorporation(UUID corporationId) {
        return this.corporationRepository.findById(
                        corporationId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : corporationId
                )
                .orElseThrow(() -> new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage()));
    }
}
