package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 해당 내용 들을 가지고 schema 생성
 */
@Data
@Builder
public class Schema {

    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;
}
