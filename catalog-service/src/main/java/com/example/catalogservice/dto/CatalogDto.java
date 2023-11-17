package com.example.catalogservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CatalogDto implements Serializable {

    private String productId;
    private Integer qty; // 수량
    private Integer unitPrice; // 개별 단가
    private Integer totalPrice; // 전체 단가

    private String orderId;
    private String userId; // 주문자
}
