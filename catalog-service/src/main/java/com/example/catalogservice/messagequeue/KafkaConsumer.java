package com.example.catalogservice.messagequeue;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    /**
     * Listener 를 실제로 연결
     * @param kafkaMessage : Topic 에서 가져 오는 메세지
     */
    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message : {}", kafkaMessage);

        /* kafka message 는 데이터를 직렬화 해서 전달하기 때문에, 이를 역직렬화 */
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        /* 외부 데이터를 가져오는 것이기 때문에, try-catch */
        try {
            /* kafka message 는 string 형태로 들어 오기 때문에 이를 JSON type 으로 변경 */
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        /* 역직렬화가 완료 되었다면, 필요한 데이터 추출 후 사용 */
        /* map 에서 가져 오는 데이터는 Object 이기 때문에, String 으로 파싱 */
        CatalogEntity catalog = catalogRepository.findByProductId((String) map.get("productId"));

        /* 가져온 데이터가 null 이 아닐 때 */
        if (catalog != null) {
            /* 전체 수량 감소 및 update */
            catalog.setStock(catalog.getStock() - (Integer) map.get("qty"));
            catalogRepository.save(catalog);
        }
    }
}
