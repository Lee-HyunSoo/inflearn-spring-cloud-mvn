package com.example.orderservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /* 단일 주문 조회 */
    OrderEntity findByOrderId(String orderId);

    /* 주문 목록 조회 */
    Iterable<OrderEntity> findByUserId(String userId);
}
