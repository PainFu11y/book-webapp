package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.request.BookFilterRequest;
import com.epam.rd.autocode.spring.project.mapper.BookMapper;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.specifications.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookDTO> getAllBooks() {
        return bookMapper.toDTOList(bookRepository.findAll());
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Book not found with name: " + name));
        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO bookDTO) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Book not found with name: " + name));


        Book updatedBook = bookMapper.toEntity(bookDTO);
        updatedBook.setId(book.getId());

        updatedBook = bookRepository.save(updatedBook);
        return bookMapper.toDTO(updatedBook);
    }

    @Override
    public void deleteBookByName(String name) {
        bookRepository.deleteByName(name);
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        return bookMapper.toDTO(book);
    }

    public Page<Book> findAllFiltered(BookFilterRequest filter, Pageable pageable) {
        Pageable checkedPageable = sanitizePageable(pageable);


        Specification<Book> spec = Specification.where(
                        BookSpecification.nameContains(filter.getName()))
                .and(BookSpecification.genreEquals(filter.getGenre()))
                .and(BookSpecification.authorContains(filter.getAuthor()))
                .and(BookSpecification.languageEquals(filter.getLanguage()))
                .and(BookSpecification.ageGroupEquals(filter.getAgeGroup()))
                .and(BookSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(BookSpecification.publishedBetween(filter.getFromDate(), filter.getToDate()));

        return bookRepository.findAll(spec, checkedPageable);
    }

    private Pageable sanitizePageable(Pageable pageable) {
        List<String> validProperties = List.of(
                "name", "genre", "author", "language", "ageGroup", "price", "publicationDate", "pages"
        );

        Sort sort = Sort.unsorted();
        for (Sort.Order order : pageable.getSort()) {
            if (validProperties.contains(order.getProperty())) {
                sort = sort.and(Sort.by(order));
            }
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }


}

