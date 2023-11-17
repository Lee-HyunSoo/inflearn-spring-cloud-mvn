package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 실제 저장할 데이터
 */
@Data
@Builder
public class Payload {

    /* 실제 DB 내 Order Table column 명 */
    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int unit_price;
    private int total_price;
}
