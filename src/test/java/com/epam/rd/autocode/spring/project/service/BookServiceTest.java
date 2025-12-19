package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.request.BookFilterRequest;
import com.epam.rd.autocode.spring.project.mapper.BookMapper;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBooksShouldReturnDTOList() {
        Book book1 = new Book();
        Book book2 = new Book();

        BookDTO dto1 = new BookDTO();
        BookDTO dto2 = new BookDTO();

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookMapper.toDTOList(List.of(book1, book2))).thenReturn(List.of(dto1, dto2));

        List<BookDTO> result = bookService.getAllBooks();

        assertThat(result).hasSize(2);
        verify(bookRepository).findAll();
    }

    @Test
    void getBookByNameShouldReturnDTO() {
        Book book = new Book();
        BookDTO dto = new BookDTO();

        when(bookRepository.findByName("Book1")).thenReturn(Optional.of(book));
        when(bookMapper.toDTO(book)).thenReturn(dto);

        BookDTO result = bookService.getBookByName("Book1");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getBookByNameShouldThrowExceptionIfNotFound() {
        when(bookRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getBookByName("Unknown"));
    }

    @Test
    void addBookShouldSaveAndReturnDTO() {
        BookDTO dto = new BookDTO();
        Book entity = new Book();
        BookDTO savedDto = new BookDTO();

        when(bookMapper.toEntity(dto)).thenReturn(entity);
        when(bookRepository.save(entity)).thenReturn(entity);
        when(bookMapper.toDTO(entity)).thenReturn(savedDto);

        BookDTO result = bookService.addBook(dto);

        assertThat(result).isEqualTo(savedDto);
        verify(bookRepository).save(entity);
    }

    @Test
    void updateBookByNameShouldUpdateAndReturnDTO() {
        Book existing = new Book();
        existing.setId(1L);
        BookDTO dto = new BookDTO();
        Book updatedEntity = new Book();
        updatedEntity.setId(1L);
        BookDTO updatedDto = new BookDTO();

        when(bookRepository.findByName("OldBook")).thenReturn(Optional.of(existing));
        when(bookMapper.toEntity(dto)).thenReturn(updatedEntity);
        when(bookRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(bookMapper.toDTO(updatedEntity)).thenReturn(updatedDto);

        BookDTO result = bookService.updateBookByName("OldBook", dto);

        assertThat(result).isEqualTo(updatedDto);
        assertThat(updatedEntity.getId()).isEqualTo(existing.getId());
    }

    @Test
    void deleteBookByNameShouldCallRepository() {
        bookService.deleteBookByName("Book1");

        verify(bookRepository).deleteByName("Book1");
    }

    @Test
    void findAllFilteredShouldReturnPage() {
        BookFilterRequest filter = new BookFilterRequest();
        filter.setName("Book");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(new Book()));


        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<Book> result = bookService.findAllFiltered(filter, pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}
