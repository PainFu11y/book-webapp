package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookItemDTO {


    private BookDTO book;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}