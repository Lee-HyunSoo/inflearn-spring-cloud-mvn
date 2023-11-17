package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order-service")
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in Catalog Service on PORT %s", env.getProperty("local.server.port"));
    }

    // http://localhost:port/order-service/{userId}/orders
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId,
                                                     @RequestBody RequestOrder requestOrder) {
        log.info("Before add orders data");
        ModelMapper mapper = new ModelMapper();
        /* 매칭 전략을 STRICT, 강하게 설정해서 반드시 값이 맞아 떨어져야 변환 가능 */
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        /* RequestOrder -> OrderDto */
        OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
        /* OrderDto 에 주문한 사람 set */
        orderDto.setUserId(userId);
        /* use JPA */
        /* 주문 생성 */
        OrderDto createdOrderDto = orderService.createOrder(orderDto);
        ResponseOrder result = mapper.map(createdOrderDto, ResponseOrder.class);

        /* use kafka */
        /* 더이상 service 메서드를 호출 하지 않기 때문에, 기존에 service 에서 실행하던 orderDto 의 변경 내역을 컨트롤러에서 실행한다. */
        /* 고유한 orderId 부여 */
//        orderDto.setOrderId(UUID.randomUUID().toString());
        /* totalPrice 계산 */
//        orderDto.setTotalPrice(requestOrder.getQty() * requestOrder.getUnitPrice());
        /* 반환 값 설정 */
//        ResponseOrder result = mapper.map(orderDto, ResponseOrder.class);

        /* 데이터를 Kafka 에 메세지 형태로 전달  */
        /* Kafka 에 주문 서비스를 전달 하는 작업 */
        /* consumer 가 구독 하고 있는 topic 이름, 전달할 데이터를 parameter 로 사용 */
        kafkaProducer.send("example-catalog-topic", orderDto);
        /* kafka sink connect 를 사용해 jpa 대신 DB Update */
//        orderProducer.send("orders", orderDto);

        log.info("After added orders data");
        /* createdOrderDto -> ResponseOrder */
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable String userId) throws Exception {
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);

        /* ResponseOrder List 생성 */
        List<ResponseOrder> result = new ArrayList<>();
        /* OrderEntity List -> ResponseOrder List */
        orders.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        /* 강제 예외 처리 */
//        try {
//            Thread.sleep(1000);
//            throw new Exception("장애 발생");
//        } catch (InterruptedException e) {
//            log.warn(e.getMessage());
//        }

        log.info("After retrieved orders data");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
