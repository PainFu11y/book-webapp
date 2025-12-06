package com.epam.rd.autocode.spring.project.dto.request.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderDTO {

    @NotNull
    private String employeeEmail;

    @NotNull
    private LocalDateTime orderDate;

    @NotEmpty(message = "Order must contain at least one book item")
    @Valid
    private List<CreateBookItemDTO> items;

}
