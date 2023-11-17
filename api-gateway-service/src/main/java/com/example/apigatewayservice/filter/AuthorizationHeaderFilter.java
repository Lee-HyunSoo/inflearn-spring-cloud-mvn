package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        /* exchange 를 통해 Request, Response 객체를 얻을 수 있다. */
        return (exchange, chain) -> {
            /* 로그인 시 받았던 토큰을 헤더에 전달하기 위한 Request */
            ServerHttpRequest request = exchange.getRequest();

            /* 헤더에 토큰 정보가 있는지 확인 */
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            /* 헤더에서 토큰 정보 추출, 이 때 리스트 형태로 반환 되기 때문에 0번 index 값을 사용 */
            /* 해당 토큰 내부에 Bearer Token 이 들어있다. */
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            /* 토큰에 함께 작성되어 있는 Bearer 라는 글자 제거 */
            String jwt = authorizationHeader.replace("Bearer", "");

            /* JWT 가 유효한지 확인 */
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT Token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    /**
     * JWT 가 정상적인지 확인
     */
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;
        String subject = null;

        try {
            /* subject 추출 */
            subject = Jwts.parser()
                    /* 토큰 생성 시 사용한 알고리즘에 사용 된 데이터를 통해 복호화 */
                    .setSigningKey(env.getProperty("token.secret"))
                    /* 복호화 대상 지정 */
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception exception) {
            returnValue = false;
        }

        /* subject 가 비어 있으면 -> 정상 토큰이 아니기 때문에 false */
        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

    /**
     * api-gateway-service 는 Spring MVC 가 아니라 Spring Web Flux 로 구성되어 있기 때문에 Mono 형태의 method
     * functional api 를 사용함으로서 비동기 방식으로 api 처리
     * 단일값 처리 : Mono
     * 단일값 아닌 형태의 데이터 : Flux
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        /* exchange 로 response 객체 사용 */
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        /* setComplete() 를 통해 response 객체를 Mono Type 으로 반환 */
        return response.setComplete();
    }
}
