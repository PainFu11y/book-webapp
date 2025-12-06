package java.com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.mapper.BookMapper;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
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
}

