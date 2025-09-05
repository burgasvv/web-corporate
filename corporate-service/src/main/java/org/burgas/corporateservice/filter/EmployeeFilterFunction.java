package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.employee.EmployeeRequest;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Employee;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.entity.OfficePK;
import org.burgas.corporateservice.exception.*;
import org.burgas.corporateservice.service.CorporationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

import static org.burgas.corporateservice.message.CorporationMessages.*;
import static org.burgas.corporateservice.message.EmployeeMessages.EMPLOYEE_NOT_FOUND;
import static org.burgas.corporateservice.message.EmployeeMessages.IDENTITY_NOT_EMPLOYEE;
import static org.burgas.corporateservice.message.IdentityMessages.IDENTITY_NOT_AUTHENTICATED;
import static org.burgas.corporateservice.message.IdentityMessages.IDENTITY_NOT_AUTHORIZED;

@Component
@RequiredArgsConstructor
public class EmployeeFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CorporationService corporationService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/employees/create") || request.path().equals("/api/v1/employees/update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                EmployeeRequest employeeRequest = request.body(EmployeeRequest.class);
                UUID identityId = employeeRequest.getIdentityId();
                Identity identity = (Identity) authentication.getPrincipal();

                if (identity.getId().equals(identityId)) {
                    Corporation corporation = this.corporationService.findCorporation(employeeRequest.getOffice().getCorporationId());

                    if (corporation.getDirectors().contains(identity.getId())) {
                        request.attributes().put("employeeRequest", employeeRequest);
                        return next.handle(request);

                    } else {
                        throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                    }

                } else {
                    throw new IdentityNotAuthorizedException(IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/employees/delete")
        ) {

            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                UUID employeeId = request.param("employeeId")
                        .map(UUID::fromString)
                        .orElseThrow();
                Identity identity = (Identity) authentication.getPrincipal();

                if (identity.getEmployee().getId().equals(employeeId)) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotEmployeeException(IDENTITY_NOT_EMPLOYEE.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/employees/office-transfer")
        ) {

            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                Identity identity = (Identity) authentication.getPrincipal();
                Employee employee = identity.getEmployee();

                if (employee != null) {

                    UUID employeeId = request.param("employeeId")
                            .map(UUID::fromString)
                            .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND.getMessage()));
                    OfficePK officePK = request.body(OfficePK.class);

                    if (officePK.getCorporationId().equals(employee.getOffice().getOfficePK().getCorporationId())) {
                        Corporation corporation = this.corporationService.findCorporation(officePK.getCorporationId());

                        if (employee.getId().equals(employeeId) || corporation.getDirectors().contains(identity.getId())) {
                            request.attributes().put("officePK", officePK);
                            return next.handle(request);

                        }  else {
                            throw new WrongEmployeeOrCorporationException(WRONG_EMPLOYEE_OR_CORPORATION.getMessage());
                        }

                    } else {
                        throw new WrongCorporationException(WRONG_CORPORATION.getMessage());
                    }

                } else {
                    throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }
        }

        return next.handle(request);
    }
}
