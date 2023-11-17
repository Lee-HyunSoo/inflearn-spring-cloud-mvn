package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(LoggingFilter.Config config) {
//        // Logging Pre Filter
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());
//
//            if (config.isPreLogger()) {
//                log.info("Logging Filter start: request id -> {}", request.getId());
//            }
//
//            // Custom Post Filter
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                if (config.isPreLogger()) {
//                    log.info("Logging POST Filter: response code -> {}", response.getStatusCode());
//                }
//            }));
//        };

        /* OrderedGatewayFilter 생성자의 파라미터 */
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Logging Filter start: request id -> {}", request.getId());
            }

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPreLogger()) {
                    log.info("Logging POST Filter: response code -> {}", response.getStatusCode());
                }
            }));
            /* 우선순위를 정해주는 int order */
        }, Ordered.LOWEST_PRECEDENCE);

        return filter;
    }

    /**
     * application.yml 에서 가져 오는 값
     */
    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean poseLogger;
    }
}
