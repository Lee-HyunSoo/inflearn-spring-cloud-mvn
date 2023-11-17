package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;

public interface OrderService {

    /* 주문 생성 */
    OrderDto createOrder(OrderDto orderDto);

    /* orderId를 통해 주문 검색 */
    OrderDto getOrderByOrderId(String orderId);

    /* 특정 사용자의 주문목록 검색 */
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
