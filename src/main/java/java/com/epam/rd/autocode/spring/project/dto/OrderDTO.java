package java.com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @NotBlank(message = "Client email cannot be blank")
    @Email(message = "Client email should be valid")
    private String clientEmail;

    @NotBlank(message = "Employee email cannot be blank")
    @Email(message = "Employee email should be valid")
    private String employeeEmail;

    @NotNull(message = "Order date cannot be null")
    private LocalDateTime orderDate;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotEmpty(message = "Order must contain at least one book item")
    @Valid
    private List<BookItemDTO> bookItems;

    OrderStatus orderStatus;
}