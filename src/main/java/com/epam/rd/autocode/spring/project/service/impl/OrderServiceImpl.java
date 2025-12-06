package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.request.create.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.enums.UserRole;
import com.epam.rd.autocode.spring.project.exception.AccessDeniedException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.mapper.OrderMapper;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.*;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<OrderDTO> getOrders() {
        String email = getCurrentUserEmail();
        String role = getCurrentUserRole();


        if (role == null) {
            return List.of();
        }

        List<Order> orders = switch (role) {
            case "CLIENT" -> orderRepository.findByClientEmail(email);
            case "EMPLOYEE" -> orderRepository.findByEmployeeEmail(email);
            default -> List.of();
        };

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }


    @Override
    @Transactional
    public OrderDTO addOrder(CreateOrderDTO dto) {

        Client client = clientRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Employee employee = employeeRepository.findByEmail(dto.getEmployeeEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        Order order = new Order();
        order.setOrderDate(dto.getOrderDate());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setClient(client);
        order.setEmployee(employee);


        List<BookItem> items = dto.getItems().stream()
                .map(b -> {
                    Book book = bookRepository.findById(b.getBookId())
                            .orElseThrow(() -> new NotFoundException("Book not found: " + b.getBookId()));

                    BookItem bi = new BookItem();
                    bi.setBook(book);
                    bi.setQuantity(b.getQuantity());
                    bi.setOrder(order);
                    return bi;
        }).toList();

        BigDecimal totalPrice = items.stream()
                .map(i -> i.getBook().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        order.setBookItems(items);
        order.setPrice(totalPrice);
        order.setOrderStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        return orderMapper.toDTO(order);
    }

    // Submit order by Client
    public void submitOrder(Long orderId) {
        String clientEmail = getCurrentUserEmail();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getClient().getEmail().equals(clientEmail)) {
            throw new AccessDeniedException("You can't submit someone else's order");
        }

        order.setOrderStatus(OrderStatus.SUBMITTED);
        orderRepository.save(order);
    }

    // Confirm order by employee
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }


    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentUserRole() {
        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        if (role == null) return null;

        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return UserRole.valueOf(role).toString();
    }
}

