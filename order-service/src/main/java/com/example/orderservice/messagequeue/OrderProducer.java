package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /* field 정보 생성 */
    /* 배열 데이터를 List 형태로 만들기 위해 asList method 사용  */
    List<Field> fields = Arrays.asList(
            new Field("string", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "qty"),
            new Field("int32", true, "unit_price"),
            new Field("int32", true, "total_price"));

    /* field 정보를 사용해 schema 생성 */
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();

    /**
     * 데이터를 json format 으로 전달 하기 위해 변환 과정을 거친다.
     * kafkaTemplate.send(topic, json 포멧 데이터) 를 통해 최종 send
     * @param topic : 전달 하는 topic 의 이름
     * @param orderDto : 전달 하려는 데이터
     */
    public OrderDto send(String topic, OrderDto orderDto) {
        /* 실제 전달 되는 값들이 저장 되어 있는 Payload 생성 */
        Payload payload = Payload.builder()
                .order_id(orderDto.getOrderId())
                .user_id(orderDto.getUserId())
                .product_id(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unit_price(orderDto.getUnitPrice())
                .total_price(orderDto.getTotalPrice())
                .build();

        /* topic 에 전달할 객체인 KafkaOrderDto 생성 */
        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);

        /* 주문 서비스 (kafkaOrderDto) 를 json format 으로 변경 */
        ObjectMapper mapper = new ObjectMapper();
        /* 전달할 때는 String 값으로 전달 */
        String jsonInString = "";

        /* 외부에 데이터를 보낼 것이기 때문에 try-catch */
        try {
            /* 해당 String 의 모양이 JSON 형태를 가질 수 있도록 mapper 기능 사용 */
            jsonInString = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        /* message 를 실제로 send */
        kafkaTemplate.send(topic, jsonInString);
        log.info("Order Producer sent data from Order Microservice : {}", kafkaOrderDto);

        return orderDto;
    }
}
