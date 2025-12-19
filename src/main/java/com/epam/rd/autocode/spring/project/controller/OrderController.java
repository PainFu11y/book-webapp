package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.request.create.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

        // CLIENT METHODS
        @Operation(summary = "Create a new order for the current client")
        @PostMapping("/client")
        public OrderDTO createOrder(@RequestBody CreateOrderDTO dto) {
            return orderService.addOrder(dto);
        }

        @Operation(summary = "Get all orders of the current client")
        @GetMapping("/client")
        public List<OrderDTO> getClientOrders() {
            return orderService.getOrders();
        }

        @Operation(summary = "Submit an order")
        @PostMapping("/client{orderId}")
        public void submitOrder(@PathVariable Long orderId) {
            orderService.submitOrder(orderId);
        }


        // EMPLOYEE METHODS
        @Operation(summary = "Get all orders assigned to the current employee")
        @GetMapping("/employee")
        public List<OrderDTO> getEmployeeOrders() {
            return orderService.getOrders();
        }

        @Operation(summary = "Confirm a client order")
        @PostMapping("/employee/confirm/{orderId}")
        public void confirmOrder(@PathVariable Long orderId) {
            orderService.confirmOrder(orderId);
        }
        @Operation(summary = "Cancel a client order")
        @PostMapping("/employee/cancel/{orderId}")
        public void cancelOrder(@PathVariable Long orderId) {
            orderService.cancelOrder(orderId);
        }

}
