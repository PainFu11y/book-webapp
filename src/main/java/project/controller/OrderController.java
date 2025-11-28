package project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/client/{email}")
    @Operation(summary = "Get orders by client email")
    public List<OrderDTO> getOrdersByClient(@PathVariable String email) {
        return orderService.getOrdersByClient(email);
    }

    @GetMapping("/employee/{email}")
    @Operation(summary = "Get orders by employee email")
    public List<OrderDTO> getOrdersByEmployee(@PathVariable String email) {
        return orderService.getOrdersByEmployee(email);
    }

    @PostMapping
    @Operation(summary = "Add a new order")
    public ResponseEntity<OrderDTO> addOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO savedOrder = orderService.addOrder(orderDTO);
        return ResponseEntity.ok(savedOrder);
    }
}
