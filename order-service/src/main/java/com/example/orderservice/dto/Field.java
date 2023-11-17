package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 데이터를 저장하기 위해 DB 의 어떤 field 가 사용 될 것인지 지정
 * Field : kafka connect 를 통해 source connector -> sink connector 로 데이터를 보낼 때 어떤 정보가 저장될 것인지 구성 되어 있다.
 */
@Data
@AllArgsConstructor
public class Field {

    private String type;
    private boolean optional;
    private String field;
}
