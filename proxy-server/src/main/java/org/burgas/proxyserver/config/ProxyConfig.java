package org.burgas.proxyserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Bean
    public RouteLocator routeLocator(final RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(
                        "corporate-service",
                        predicateSpec -> predicateSpec
                                .path("/api/v1/**")
                                .uri("http://localhost:9000")
                )
                .build();
    }
}
