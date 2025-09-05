package org.burgas.corporateservice.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.department.DepartmentRequest;
import org.burgas.corporateservice.entity.Corporation;
import org.burgas.corporateservice.entity.Department;
import org.burgas.corporateservice.entity.Employee;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.exception.CorporationNotFoundException;
import org.burgas.corporateservice.exception.EmployeeNotFoundException;
import org.burgas.corporateservice.exception.IdentityNotAuthenticatedException;
import org.burgas.corporateservice.exception.IdentityNotDirectorException;
import org.burgas.corporateservice.service.CorporationService;
import org.burgas.corporateservice.service.DepartmentServiceImpl;
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
import static org.burgas.corporateservice.message.EmployeeMessages.EMPLOYEE_NOT_FOUND;
import static org.burgas.corporateservice.message.IdentityMessages.IDENTITY_NOT_AUTHENTICATED;

@Component
@RequiredArgsConstructor
public class DepartmentFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CorporationService corporationService;
    private final DepartmentServiceImpl departmentService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/departments/by-corporation")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                UUID corporationId = request.param("corporationId")
                        .map(UUID::fromString)
                        .orElseThrow();

                Identity identity = (Identity) authentication.getPrincipal();

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
                    throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/departments/by-id")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {

                UUID departmentId = request.param("departmentId")
                        .map(UUID::fromString)
                        .orElseThrow();

                Department department = this.departmentService.findDepartment(departmentId);

                Identity identity = (Identity) authentication.getPrincipal();
                Employee departmentEmployee = department.getOffices()
                        .stream()
                        .flatMap(office -> office.getEmployees().stream())
                        .filter(employee -> employee.getIdentity().getId().equals(identity.getId()))
                        .findFirst()
                        .orElse(null);

                if (departmentEmployee != null) {
                    return next.handle(request);

                } else {
                    throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/departments/create")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                DepartmentRequest departmentRequest = request.body(DepartmentRequest.class);
                UUID corporationId = departmentRequest.getCorporationId();

                if (corporationId != null) {
                    Corporation corporation = this.corporationService.findCorporation(corporationId);
                    Identity identity = (Identity) authentication.getPrincipal();

                    if (corporation.getDirectors().contains(identity.getId())) {
                        request.attributes().put("departmentRequest", departmentRequest);
                        return next.handle(request);

                    } else {
                        throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                    }

                } else {
                    throw new CorporationNotFoundException(CORPORATION_NOT_FOUND.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/departments/update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                DepartmentRequest departmentRequest = request.body(DepartmentRequest.class);
                UUID departmentId = departmentRequest.getId();

                Department department = this.departmentService.findDepartment(departmentId);
                Corporation corporation = department.getCorporation();
                Identity identity = (Identity) authentication.getPrincipal();

                if (corporation.getDirectors().contains(identity.getId())) {
                    request.attributes().put("departmentRequest", departmentRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotDirectorException(IDENTITY_NOT_DIRECTOR.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/departments/delete")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                UUID departmentId = request.param("departmentId")
                        .map(UUID::fromString)
                        .orElseThrow();

                Department department = this.departmentService.findDepartment(departmentId);
                Corporation corporation = department.getCorporation();
                Identity identity = (Identity) authentication.getPrincipal();

                if (corporation.getDirectors().contains(identity.getId())) {
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
