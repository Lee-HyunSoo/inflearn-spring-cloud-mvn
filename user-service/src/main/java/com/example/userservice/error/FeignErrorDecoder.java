package com.example.userservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    /* 이렇게 생성자 주입을 받아서 쓰기 위해서는 해당 클래스가 컴포넌트로 등록되어 있어야 한다. */
    private final Environment env;

    @Override
    public Exception decode(String methodName, Response response) {
        /* switch-case 를 통해 에러 코드의 분기문 작성 */
        switch (response.status()) {
            /* 400 에러일 시 그냥 break */
            case 400:
                break;
            /* method 이름에 getOrders 가 포함 될 시, 예외 처리 */
            case 404:
                if (methodName.contains("getOrders")) {
                    /* decode 메서드의 파라미터로 받은 response 객체의 status 를 통해 에러코드 추출 */
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            env.getProperty("order_service.exception.orders_is_empty"));
                }
                break;
            default:
                /* default 로 예외가 발생한 원인을 반환 */
                return new Exception(response.reason());
        }
        /* switch-case 에 걸리는게 없으면 그냥 null 반환 */
        return null;
    }
}
