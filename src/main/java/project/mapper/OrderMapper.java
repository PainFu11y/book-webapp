package project.mapper;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ModelMapper modelMapper;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @PostConstruct
    public void setupMapper() {
        // DTO to Entity
        modelMapper.createTypeMap(OrderDTO.class, Order.class);

        // Entity to DTO
        modelMapper.createTypeMap(Order.class, OrderDTO.class);
    }

    public Order toEntity(OrderDTO dto) {
        Order order = modelMapper.map(dto, Order.class);

        // client by email
        Client client = clientRepository.findByEmail(dto.getClientEmail())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        order.setClient(client);

        // employee by email
        Employee employee = employeeRepository.findByEmail(dto.getEmployeeEmail())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        order.setEmployee(employee);

        // BookItems
        List<BookItem> items = dto.getBookItems().stream()
                .map(itemDto -> toBookItemEntity(itemDto, order))
                .collect(Collectors.toList());
        order.setBookItems(items);

        return order;
    }

    public OrderDTO toDTO(Order order) {
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        dto.setClientEmail(order.getClient().getEmail());
        dto.setEmployeeEmail(order.getEmployee().getEmail());

        List<BookItemDTO> bookItemDtos = order.getBookItems().stream()
                .map(this::toBookItemDTO)
                .collect(Collectors.toList());
        dto.setBookItems(bookItemDtos);

        return dto;
    }

    private BookItem toBookItemEntity(BookItemDTO dto, Order order) {
        BookItem item = modelMapper.map(dto, BookItem.class);
        item.setOrder(order);
        return item;
    }

    private BookItemDTO toBookItemDTO(BookItem item) {
        return modelMapper.map(item, BookItemDTO.class);
    }
}


