package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.request.create.CreateOrderDTO;

import java.util.*;

public interface OrderService {

    List<OrderDTO> getOrders();

    OrderDTO addOrder(CreateOrderDTO order);

    void submitOrder(Long orderId);
    void confirmOrder(Long orderId);
    void cancelOrder(Long orderId);
}
