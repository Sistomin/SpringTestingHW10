package ru.gb.SpringTesting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.SpringTesting.aspect.Timer;
import ru.gb.SpringTesting.exception.NotFoundEntityException;
import ru.gb.SpringTesting.model.Book;
import ru.gb.SpringTesting.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/book")
@Tag(name = "Books")
@Timer
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Загружает книгу с указанным идентификатором в пути")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<Book> getByID(@PathVariable Long id) {
        Book book = bookService.getByID(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Загружает список всех книг, которые есть в библиотеке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all books",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})
    })
    public ResponseEntity<List<Book>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @PostMapping
    @Operation(summary = "Create new book", description = "Создаёт новую книгу в библиотеке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Create new book",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})
    })
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return new ResponseEntity<>(bookService.create(book), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book by id", description = "Обновляет свойства книги с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the book",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Book for update not found",
                    content = @Content)
    })
    public ResponseEntity<Book> update(@PathVariable("id") Long id, @RequestBody Book book) {
        Book updatedBook = bookService.update(id, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by id", description = "Удаляет книгу с указанным идентификатором из библиотеки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the book",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book for deletion not found",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        bookService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Метод для обработки исключения в случае некорректно указанного ID книги
     *
     * @return ответ со статусом 404
     */
    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> processNotFoundEntityException(NotFoundEntityException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
