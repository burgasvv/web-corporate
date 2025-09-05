package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.position.PositionRequest;
import org.burgas.corporateservice.entity.*;
import org.burgas.corporateservice.exception.IdentityNotAuthenticatedException;
import org.burgas.corporateservice.exception.IdentityNotDirectorException;
import org.burgas.corporateservice.exception.IdentityNotEmployeeException;
import org.burgas.corporateservice.service.CorporationService;
import org.burgas.corporateservice.service.DepartmentServiceImpl;
import org.burgas.corporateservice.service.PositionServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

import static org.burgas.corporateservice.message.EmployeeMessages.IDENTITY_NOT_DIRECTOR;
import static org.burgas.corporateservice.message.EmployeeMessages.IDENTITY_NOT_EMPLOYEE;
import static org.burgas.corporateservice.message.IdentityMessages.IDENTITY_NOT_AUTHENTICATED;

@Component
@RequiredArgsConstructor
public class PositionFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final PositionServiceImpl positionService;
    private final DepartmentServiceImpl departmentService;
    private final CorporationService corporationService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/positions/by-department")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();

                UUID departmentId = request.param("departmentId")
                        .map(UUID::fromString)
                        .orElseThrow();

                Department department = this.departmentService.findDepartment(departmentId);

                Employee departmentEmployee = department.getOffices()
                        .stream()
                        .flatMap(office -> office.getEmployees().stream())
                        .filter(employee -> employee.getIdentity().getId().equals(identity.getId()))
                        .findFirst()
                        .orElse(null);

                if (departmentEmployee != null) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotEmployeeException(IDENTITY_NOT_EMPLOYEE.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/positions/by-corporation")
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

                Employee corporationEmployee = corporation.getDepartments()
                        .stream()
                        .flatMap(department -> department.getOffices().stream())
                        .flatMap(office -> office.getEmployees().stream())
                        .filter(employee -> employee.getIdentity().getId().equals(identity.getId()))
                        .findFirst()
                        .orElse(null);

                if (corporationEmployee != null) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotEmployeeException(IDENTITY_NOT_EMPLOYEE.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/positions/by-id") ||
                request.path().equals("/api/v1/positions/delete")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();

                UUID positionId = request.param("positionId")
                        .map(UUID::fromString)
                        .orElseThrow();

                Position position = this.positionService.findPosition(positionId);

                Corporation corporation = position.getDepartment().getCorporation();

                if (corporation.getDirectors().contains(identity.getId())) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/positions/create")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();

                PositionRequest positionRequest = request.body(PositionRequest.class);
                UUID departmentId = positionRequest.getDepartmentId();

                Department department = this.departmentService.findDepartment(departmentId);
                Corporation corporation = department.getCorporation();

                if (corporation.getDirectors().contains(identity.getId())) {
                    request.attributes().put("positionRequest", positionRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotEmployeeException(IDENTITY_NOT_EMPLOYEE.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/positions/update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();

                PositionRequest positionRequest = request.body(PositionRequest.class);
                UUID positionId = positionRequest.getId();

                Position position = this.positionService.findPosition(positionId);
                Corporation corporation = position.getDepartment().getCorporation();

                if (corporation.getDirectors().contains(identity.getId())) {
                    request.attributes().put("positionRequest", positionRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }
        }

        return next.handle(request);
    }
}
