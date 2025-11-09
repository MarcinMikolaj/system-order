package com.project.order_system_producer_service.producer;

import com.project.order_system_producer_service.domain.Order;

public interface OrderProducer {

    void send(Order order);

}
