package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

    /**
     * application.yml 에 작성하던 내용을 java code로 작성하는 것과 동일하다.
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                /* 매개변수로 전달되어 있는 route 객체에 path 정보 등록 */
                .route(r -> r
                        .path("/first-service/**")
                        /* 요청 시 requestHeader, 응답 시 responseHeader 추가 */
                        .filters(f -> f
                                .addRequestHeader("first-request", "first-request-header")
                                .addResponseHeader("first-response", "first-response-header"))
                        .uri("http://localhost:8081"))
                /* 매개변수로 전달되어 있는 route 객체에 path 정보 등록 */
                .route(r -> r
                        .path("/second-service/**")
                        /* 요청 시 requestHeader, 응답 시 responseHeader 추가 */
                        .filters(f -> f
                                .addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }
}
