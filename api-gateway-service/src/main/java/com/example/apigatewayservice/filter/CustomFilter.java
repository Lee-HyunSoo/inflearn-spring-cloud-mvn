package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    /**
     * 부모 클래스의 Config Class 사용
     */
    public CustomFilter() {
        super(Config.class);
    }

    /**
     * 표기를 위한 Config Class
     */
    public static class Config {
        // Put use configuration
    }

    @Override
    public GatewayFilter apply(Config config) {
        /* Custom Pre Filter */
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE Filter: request id -> {}", request.getId());

            /* Custom Post Filter */
            /* filter 정의가 잘 되었다면, then 호출 */
            /* Mono : spring5 에서 지원하는 webflux 기능, 비동기 방식 서버 지원 시 단일 값을 전달할 때 사용하는 타입 */
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST Filter: response code -> {}", response.getStatusCode());
            }));
        };
    }
}
