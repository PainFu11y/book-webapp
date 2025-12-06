package java.com.epam.rd.autocode.spring.project.mapper;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.Book;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {
        modelMapper.typeMap(BookDTO.class, Book.class)
                .addMappings(mapper -> {
                    mapper.skip(Book::setId);
                });
    }

    // DTO to Entity
    public Book toEntity(BookDTO dto) {
        return modelMapper.map(dto, Book.class);
    }

    // Entity to DTO
    public BookDTO toDTO(Book entity) {
        return modelMapper.map(entity, BookDTO.class);
    }

    public List<Book> toEntityList(List<BookDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<BookDTO> toDTOList(List<Book> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
