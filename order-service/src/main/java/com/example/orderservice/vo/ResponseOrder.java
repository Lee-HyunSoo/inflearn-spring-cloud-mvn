package com.example.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
/* 반환 시 null 값 방지를 위해 non null option 추가 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {

    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private Date createdAt;

    private String orderId;
}
