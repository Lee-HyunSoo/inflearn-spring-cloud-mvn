package com.example.orderservice.messagequeue;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    /**
     * Kafka 에 접속하기 위한 정보가 들어가 있는 Bean
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        /* properties 에 정보 저장 */
        Map<String, Object> properties = new HashMap<>();

        /* Kafka Server IP */
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.0.101:9092");

        /* key, value 값을 Serializer, topic 에 반영 */
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    /**
     * Kafka 토픽에 데이터를 보내기 위해 사용 하는 객체
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        /* 객체 생성 시 접속 정보를 가지고 있는 ProducerFactory 의 return 값을 등록 */
        return new KafkaTemplate<>(producerFactory());
    }
}
