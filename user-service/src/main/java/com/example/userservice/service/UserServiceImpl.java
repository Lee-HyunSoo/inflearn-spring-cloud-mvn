package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.error.FeignErrorDecoder;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Environment env;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /* 해당 서비스는 username == 우리가 사용 하는 email 이다. */
        UserEntity userEntity = userRepository.findByEmail(username);

        /* 없는 유저일 경우, exception */
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        /* 유저가 존재하면, SpringSecurity 내 User Type 으로 변환 후 return */
        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPwd(),
                true,
                true,
                true,
                true,
                new ArrayList<>()); // 권한 목록
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        /* Random UUID를 사용해 UserId 부여  */
        userDto.setUserId(UUID.randomUUID().toString());

        /* model mapper를 사용해 dto -> entity */
        ModelMapper mapper = new ModelMapper();
        /* 매칭 전략을 STRICT, 강하게 설정해서 반드시 값이 맞아 떨어져야 변환 가능  */
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        /* dto -> entity */
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        /* BCrypt Algorithm 을 사용해 password 암호화 */
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        /* DB Insert */
        UserEntity savedUser = userRepository.save(userEntity);

        /* savedEntity -> dto */
        return mapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        /* user 조회 */
        UserEntity userEntity = userRepository.findByUserId(userId);
        /* 조회 결과가 null 이라면, exception */
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        /* UserEntity -> UserDto */
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        /* 아직 orderService가 없기 때문에, 임시로 작성 */
//        List<ResponseOrder> orders = new ArrayList<>();

        /** Use RestTemplate */
        /* order-service 내 controller 의 api uri : @PostMapping("/{userId}/orders") */
        /* api 요청에 필요한 {userId} 를 위해 %s 를 사용 */
//        String orderUrl = "http://127.0.0.1:8000/order-service/%s/orders";

        /* 하드 코딩 된 uri 를 yml 에서 읽어 오도록 변경, %s 의 파라미터로 userId 사용 */
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);

        /*
            주소 값
            호출 하는 메서드 타입
            요청 파라미터
            api 요청 결과를 어떤 형식으로 받을 지
        */
//        ResponseEntity<List<ResponseOrder>> ordersResponse =
//                restTemplate.exchange(
//                    orderUrl,
//                    HttpMethod.GET,
//                    null,
//                    new ParameterizedTypeReference<List<ResponseOrder>>() {
//                });

        /* ResponseEntity -> List<ResponseOrder>  */
//        List<ResponseOrder> orders = ordersResponse.getBody();

        /** Use FeignClient */
        /* FeignClient Interface 내 메서드 호출 */
//        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);

        /* try-catch 를 사용한 예외 처리 */
//        List<ResponseOrder> orders = null;
//        try {
//            orderServiceClient.getOrders(userId);
//        } catch (FeignException ex) {
//            log.error(ex.getMessage());
//        }

        /* FeignErrorDecoder 를 사용한 예외 처리 */
//        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);

        log.info("Before call orders microservice");
        /* ErrorDecoder 대신 CircuitBreaker 적용 */
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        /* circuitBreakerFactory 로 생성한 circuitBreaker 사용 */
        /* 호출 후 반환 값 설정 () -> 정상 적인 반환 값, 문제가 생겼을 시 문제가 생긴 코드 처리 */
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());
        log.info("After called orders microservice");

        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);

        /* UserEntity -> UserDto */
        return new ModelMapper().map(user, UserDto.class);
    }
}
