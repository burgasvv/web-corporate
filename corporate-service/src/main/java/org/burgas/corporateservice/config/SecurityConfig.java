package org.burgas.corporateservice.config;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.burgas.corporateservice.entity.Authority.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()))
                .cors(cors -> cors.configurationSource(new UrlBasedCorsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.securityContextRepository(new RequestAttributeSecurityContextRepository()))
                .authenticationManager(this.authenticationManager())
                .authorizeHttpRequests(
                        httpRequests -> httpRequests

                                .requestMatchers(
                                        "/api/v1/security/csrf-token",

                                        "/api/v1/identities/create", "/api/v1/identities/create/async",

                                        "/api/v1/corporations", "/api/v1/corporations/by-id",
                                        "/api/v1/corporations/async", "/api/v1/corporations/by-id/async",

                                        "/api/v1/offices/by-corporation", "/api/v1/offices/by-corporation/async",
                                        "/api/v1/offices/by-id", "/api/v1/offices/by-id/async"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/api/v1/identities/by-id", "/api/v1/identities/by-id/async",
                                        "/api/v1/identities/update", "/api/v1/identities/update/async",
                                        "/api/v1/identities/delete", "/api/v1/identities/delete/async",
                                        "/api/v1/identities/change-password", "/api/v1/identities/change-password/async",
                                        "/api/v1/identities/upload-image", "/api/v1/identities/upload-image/async",
                                        "/api/v1/identities/change-image", "/api/v1/identities/change-image/async",
                                        "/api/v1/identities/delete-image", "/api/v1/identities/delete-image/async"
                                )
                                .hasAnyAuthority(
                                        ADMIN.getAuthority(), USER.getAuthority(),
                                        WORKER.getAuthority(), DIRECTOR.getAuthority()
                                )

                                .requestMatchers(
                                        "/api/v1/identities/make-user", "/api/v1/identities/make-user/async",

                                        "/api/v1/employees/by-corporation", "/api/v1/employees/by-corporation/async",
                                        "/api/v1/employees/by-office", "/api/v1/employees/by-office/async",
                                        "/api/v1/employees/by-id", "/api/v1/employees/by-id/async"
                                )
                                .hasAnyAuthority(ADMIN.getAuthority(), WORKER.getAuthority(), DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/identities/make-director", "/api/v1/identities/make-director/async"
                                )
                                .hasAnyAuthority(USER.getAuthority(), WORKER.getAuthority())

                                .requestMatchers(
                                        "/api/v1/identities/make-employee", "/api/v1/identities/make-employee/async"
                                )
                                .hasAnyAuthority(USER.getAuthority(), DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/employees/create", "/api/v1/employees/create/async",
                                        "/api/v1/employees/update", "/api/v1/employees/update/async",
                                        "/api/v1/employees/delete", "/api/v1/employees/delete/async",
                                        "/api/v1/employees/office-transfer", "/api/v1/employees/office-transfer/async"
                                )
                                .hasAnyAuthority(WORKER.getAuthority(), DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/corporations/create", "/api/v1/corporations/update",
                                        "/api/v1/corporations/delete", "/api/v1/corporations/add-director",
                                        "/api/v1/corporations/create/async", "/api/v1/corporations/update/async",
                                        "/api/v1/corporations/delete/async", "/api/v1/corporations/add-director/async",

                                        "/api/v1/offices/create", "/api/v1/offices/create/async",
                                        "/api/v1/offices/update", "/api/v1/offices/update/async",
                                        "/api/v1/offices/delete", "/api/v1/offices/delete/async"
                                )
                                .hasAnyAuthority(DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/identities", "/api/v1/identities/async",
                                        "/api/v1/identities/enable-disable", "/api/v1/identities/enable-disable/async"
                                )
                                .hasAnyAuthority(ADMIN.getAuthority())
                )
                .build();
    }
}
