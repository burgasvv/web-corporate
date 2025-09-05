package org.burgas.corporateservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.entity.Media;
import org.burgas.corporateservice.exception.MediaNotFoundException;
import org.burgas.corporateservice.service.MediaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class MediaRouter {

    private final MediaService mediaService;

    @Bean
    public RouterFunction<ServerResponse> mediaRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/media/by-id", request -> {
                            Media media = this.mediaService.findById(UUID.fromString(request.param("mediaId").orElseThrow()));
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(media.getContentType()))
                                    .body(media);
                        }
                )
                .onError(
                        DataIntegrityViolationException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_ACCEPTABLE)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .onError(
                        MediaNotFoundException.class, (throwable, serverRequest) ->
                                ServerResponse
                                        .status(HttpStatus.NOT_FOUND)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(throwable.getMessage())
                )
                .build();
    }
}
