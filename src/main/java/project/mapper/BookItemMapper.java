package project.mapper;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.Order;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookItemMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {
        modelMapper.typeMap(BookItemDTO.class, BookItem.class)
                .addMappings(mapper -> mapper.skip(BookItem::setOrder));
    }

    // DTO to Entity
    public BookItem toEntity(BookItemDTO dto, Order order) {
        BookItem item = modelMapper.map(dto, BookItem.class);
        item.setOrder(order); // ВАЖНО: проставляем связь
        return item;
    }

    // Entity to DTO
    public BookItemDTO toDTO(BookItem item) {
        return modelMapper.map(item, BookItemDTO.class);
    }


    public List<BookItem> toEntityList(List<BookItemDTO> dtos, Order order) {
        return dtos.stream()
                .map(dto -> toEntity(dto, order))
                .collect(Collectors.toList());
    }

    public List<BookItemDTO> toDTOList(List<BookItem> items) {
        return items.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
