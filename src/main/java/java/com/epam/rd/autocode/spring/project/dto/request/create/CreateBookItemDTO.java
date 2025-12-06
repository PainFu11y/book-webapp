package java.com.epam.rd.autocode.spring.project.dto.request.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookItemDTO {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
