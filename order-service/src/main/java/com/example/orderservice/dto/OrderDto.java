package com.example.orderservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable {

    private String productId;
    private Integer qty; // 수량
    private Integer unitPrice; // 개별 단가
    private Integer totalPrice; // 전체 단가

    private String orderId;
    private String userId; // 주문자
}
