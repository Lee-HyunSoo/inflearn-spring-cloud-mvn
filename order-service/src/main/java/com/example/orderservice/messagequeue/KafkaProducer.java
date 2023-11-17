package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 데이터를 json format 으로 전달 하기 위해 변환 과정을 거친다.
     * kafkaTemplate.send(topic, json 포멧 데이터) 를 통해 최종 send
     * @param topic : 전달 하는 topic 의 이름
     * @param orderDto : 전달 하려는 데이터
     */
    public OrderDto send(String topic, OrderDto orderDto) {
        /* 주문 서비스 (orderDto) 를 json format 으로 변경 */
        ObjectMapper mapper = new ObjectMapper();
        /* 전달할 때는 String 값으로 전달 */
        String jsonInString = "";

        /* 외부에 데이터를 보낼 것이기 때문에 try-catch */
        try {
            /* 해당 String 의 모양이 JSON 형태를 가질 수 있도록 mapper 기능 사용 */
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        /* message 를 실제로 send */
        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data from Order Microservice : {}", orderDto);

        return orderDto;
    }
}
