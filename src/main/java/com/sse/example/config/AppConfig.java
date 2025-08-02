package com.sse.example.config;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AppConfig {

    @Bean
    public RouterFunction<ServerResponse> faviconRouter() {
        return route()
                .GET("/favicon.ico",
                        request -> ServerResponse.noContent().build())
                .build();
    }
}
