package com.example.catalogservice.messagequeue;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    /**
     * Kafka 에 접속하기 위한 정보가 들어가 있는 Bean
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        /* properties 에 정보 저장 */
        Map<String, Object> properties = new HashMap<>();

        /* Kafka Server IP */
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.0.101:9092");

        /* 토픽의 메세지를 가져 가는 consumer 들을 그룹으로 묶어 한번에 메세지 전송이 가능하다. */
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroupId");

        /* topic 에 저장 되어 있는 key, value 값을 가져와 Deserializer  */
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    /**
     * 토픽의 변경 사항을 감지 하는 이벤트 리스너, 접속 정보를 이용해 Listener 사용
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        /* 토픽에 변경 사항이 생겼을 때 이를 캐치해 DB에 반영 하는 등의 용도로 사용  */
        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        /* 위에서 선언한 접속 정보를 등록 */
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());

        return kafkaListenerContainerFactory;
    }
}
