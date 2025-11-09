package com.project.order_system_consumer_service.service.impl;

import com.project.order_system_consumer_service.domain.Order;
import com.project.order_system_consumer_service.domain.OrderEvent;
import com.project.order_system_consumer_service.repository.OrderRepository;
import com.project.order_system_consumer_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;

    @Override
    @Transactional(timeout = 3)
    @CircuitBreaker(name = "order-group-circuit-breaker")
    public Order processOrder(OrderEvent orderEvent) {
        Order order = buildOrder(orderEvent);
        return orderRepository.save(order);
    }

    private Order buildOrder(OrderEvent orderEvent) {
        return Order.builder()
                .receiverEmail(orderEvent.receiverEmail())
                .senderCountryCode(orderEvent.senderCountryCode())
                .shipmentNumber(orderEvent.shipmentNumber())
                .receiverCountryCode(orderEvent.receiverCountryCode())
                .statusCode(orderEvent.statusCode())
                .build();
    }

}
