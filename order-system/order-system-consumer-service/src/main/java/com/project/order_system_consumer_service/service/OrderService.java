package com.project.order_system_consumer_service.service;

import com.project.order_system_consumer_service.domain.Order;
import com.project.order_system_consumer_service.domain.OrderEvent;

public interface OrderService {

    Order processOrder(OrderEvent orderEvent);

}
