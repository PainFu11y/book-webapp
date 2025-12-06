package java.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{name}")
    @Operation(summary = "Get book by name")
    public BookDTO getBookByName(@PathVariable String name) {
        return bookService.getBookByName(name);
    }

    @PostMapping
    @Operation(summary = "Add a new book")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO savedBook = bookService.addBook(bookDTO);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/{name}")
    @Operation(summary = "Update book by name")
    public BookDTO updateBook(@PathVariable String name, @Valid @RequestBody BookDTO bookDTO) {
        return bookService.updateBookByName(name, bookDTO);
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Delete book by name")
    public ResponseEntity<Void> deleteBook(@PathVariable String name) {
        bookService.deleteBookByName(name);
        return ResponseEntity.noContent().build();
    }
}
