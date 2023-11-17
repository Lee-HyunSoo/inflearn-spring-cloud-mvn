package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * schema, field, payload 를 모두 저장할 수 있는 Dto Class
 */
@Data
@Builder
@AllArgsConstructor
public class KafkaOrderDto implements Serializable {

    private Schema schema;
    private Payload payload;
}
