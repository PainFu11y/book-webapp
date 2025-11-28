package project.dto;

import com.epam.rd.autocode.spring.project.model.Book;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookItemDTO {

    private Book book;
    private Integer quantity;
}