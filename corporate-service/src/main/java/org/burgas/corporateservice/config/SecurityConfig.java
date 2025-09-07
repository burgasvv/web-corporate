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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    public XorCsrfTokenRequestAttributeHandler xorCsrfTokenRequestAttributeHandler() {
        return new XorCsrfTokenRequestAttributeHandler();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    public RequestAttributeSecurityContextRepository requestAttributeSecurityContextRepository() {
        return new RequestAttributeSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.csrfTokenRequestHandler(this.xorCsrfTokenRequestAttributeHandler()))
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .httpBasic(
                        httpBasic -> httpBasic.
                                securityContextRepository(this.requestAttributeSecurityContextRepository())
                )
                .authenticationManager(this.authenticationManager())
                .authorizeHttpRequests(
                        httpRequests -> httpRequests

                                .requestMatchers(
                                        "/api/v1/security/csrf-token",

                                        "/api/v1/identities/create",

                                        "/api/v1/corporations",
                                        "/api/v1/corporations/by-id",

                                        "/api/v1/offices/by-corporation",
                                        "/api/v1/offices/by-id"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/api/v1/identities/by-id",
                                        "/api/v1/identities/update",
                                        "/api/v1/identities/delete",
                                        "/api/v1/identities/change-password",
                                        "/api/v1/identities/upload-image",
                                        "/api/v1/identities/change-image",
                                        "/api/v1/identities/delete-image"
                                )
                                .hasAnyAuthority(
                                        ADMIN.getAuthority(), USER.getAuthority(),
                                        WORKER.getAuthority(), DIRECTOR.getAuthority()
                                )

                                .requestMatchers(
                                        "/api/v1/identities/make-user"
                                )
                                .hasAnyAuthority(ADMIN.getAuthority(), WORKER.getAuthority(), DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/identities/make-director"
                                )
                                .hasAnyAuthority(USER.getAuthority(), WORKER.getAuthority())

                                .requestMatchers(
                                        "/api/v1/identities/make-employee"
                                )
                                .hasAnyAuthority(USER.getAuthority(), DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/employees/by-corporation",
                                        "/api/v1/employees/by-office",
                                        "/api/v1/employees/by-id",
                                        "/api/v1/employees/create",
                                        "/api/v1/employees/update",
                                        "/api/v1/employees/delete",
                                        "/api/v1/employees/office-transfer",

                                        "/api/v1/departments/by-corporation",
                                        "/api/v1/departments/by-id",

                                        "/api/v1/positions/by-corporation",
                                        "/api/v1/positions/by-department",
                                        "/api/v1/positions/by-id"
                                )
                                .hasAnyAuthority(WORKER.getAuthority(), DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/corporations/create",
                                        "/api/v1/corporations/update",
                                        "/api/v1/corporations/delete",
                                        "/api/v1/corporations/add-director",
                                        "/api/v1/corporations/upload-image",
                                        "/api/v1/corporations/change-image",
                                        "/api/v1/corporations/delete-image",

                                        "/api/v1/offices/create",
                                        "/api/v1/offices/update",
                                        "/api/v1/offices/delete",

                                        "/api/v1/departments/create",
                                        "/api/v1/departments/update",
                                        "/api/v1/departments/delete",

                                        "/api/v1/positions/create",
                                        "/api/v1/positions/update",
                                        "/api/v1/positions/delete"
                                )
                                .hasAnyAuthority(DIRECTOR.getAuthority())

                                .requestMatchers(
                                        "/api/v1/identities",
                                        "/api/v1/identities/enable-disable"
                                )
                                .hasAnyAuthority(ADMIN.getAuthority())
                )
                .build();
    }
}
