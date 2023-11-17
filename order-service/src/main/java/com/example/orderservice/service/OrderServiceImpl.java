package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        /* 고유한 orderId 부여 */
        orderDto.setOrderId(UUID.randomUUID().toString());
        /* totalPrice 계산 */
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        /* ModelMapper 생성, 설정 추가 */
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        /* OrderDto -> OrderEntity */
        OrderEntity order = mapper.map(orderDto, OrderEntity.class);
        /* savedOrder */
        OrderEntity savedOrder = orderRepository.save(order);

        /* return savedOrderEntity -> OrderDto */
        return mapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity order = orderRepository.findByOrderId(orderId);

        /* return OrderEntity -> OrderDto */
        return new ModelMapper().map(order, OrderDto.class);
    }

    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
