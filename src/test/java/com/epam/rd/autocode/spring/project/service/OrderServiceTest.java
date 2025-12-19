package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.request.create.CreateBookItemDTO;
import com.epam.rd.autocode.spring.project.dto.request.create.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.exception.InsufficientFundsException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.*;
import com.epam.rd.autocode.spring.project.mapper.OrderMapper;
import com.epam.rd.autocode.spring.project.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("client@example.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addOrderShouldReturnOrderDTO() {
        CreateBookItemDTO bookItemDTO = new CreateBookItemDTO(1L, 2);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setEmployeeEmail("employee@example.com");
        dto.setOrderDate(LocalDateTime.now());
        dto.setItems(List.of(bookItemDTO));

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setBalance(new BigDecimal("1000"));

        Employee employee = new Employee();
        employee.setEmail("employee@example.com");

        Book book = new Book();
        book.setId(1L);
        book.setPrice(new BigDecimal("100"));

        Order order = new Order();
        order.setPrice(new BigDecimal("200"));

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(new com.epam.rd.autocode.spring.project.dto.OrderDTO());

        var result = orderService.addOrder(dto);

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void submitOrderShouldThrowInsufficientFunds() {
        Order order = new Order();
        order.setClient(new Client());
        order.getClient().setEmail("client@example.com");
        order.setPrice(new BigDecimal("1000"));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(order.getClient()));

        order.getClient().setBalance(new BigDecimal("500"));

        assertThatThrownBy(() -> orderService.submitOrder(1L))
                .isInstanceOf(InsufficientFundsException.class);

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void confirmOrderShouldUpdateStatus() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.confirmOrder(1L);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrderShouldRefundClient() {
        Client client = new Client();
        client.setBalance(new BigDecimal("100"));
        Order order = new Order();
        order.setPrice(new BigDecimal("50"));
        order.setClient(client);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertThat(client.getBalance()).isEqualTo(new BigDecimal("150"));
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(orderRepository).save(order);
    }

}
