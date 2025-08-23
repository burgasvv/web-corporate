package org.burgas.corporateservice.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class SecurityRouter {

    @Bean
    public RouterFunction<ServerResponse> securityRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/security/csrf-token", request ->
                                ServerResponse
                                        .status(HttpStatus.OK)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(request.attribute("_csrf"))
                )
                .build();
    }
}
