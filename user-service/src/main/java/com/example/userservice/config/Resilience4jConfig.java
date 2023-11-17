package com.example.userservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    /* Custom Resilience4j CircuitBreaker 를 위한 @Bean 등록  */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        /* custom circuitBreaker */
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                /* 100번의 요청 중 4번 실패 시 circuitBreaker open */
                .failureRateThreshold(4)
                /* circuitBreaker open 상태를 1초 간 유지, 이 후 다시 재요청 */
                .waitDurationInOpenState(Duration.ofMillis(1000))
                /* circuitBreaker 가 closed 되는 순간 저장할 호출 했던 결과 값을 count 기반으로 설정  */
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                /* 2회의 count 가 마지막에 저장 된다. */
                .slidingWindowSize(2)
                .build();

        /* custom timeLimiter */
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                /* api 요청이 4초 간 응답이 없을 경우 circuitBreaker open */
                .timeoutDuration(Duration.ofSeconds(4))
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                /* custom 설정 등록 */
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build()
        );
    }
}
