package com.example.userservice.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseOrder {

    private String productId;
    private Integer qty; // 수량
    private Integer unitPrice; // 단일 물품 가격
    private Integer totalPrice; // 총 가격
    private Date createdAt;

    private String orderId; // 주문 id
}
