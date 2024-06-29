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
import ru.gb.SpringTesting.aspect.RecoverException;
import ru.gb.SpringTesting.aspect.Timer;
import ru.gb.SpringTesting.exception.NotFoundEntityException;
import ru.gb.SpringTesting.exception.UserIssueLimitExceededException;
import ru.gb.SpringTesting.model.Issue;
import ru.gb.SpringTesting.model.Reader;
import ru.gb.SpringTesting.service.ReaderService;

import java.util.List;

@RestController
@RequestMapping("/reader")
@Tag(name = "Readers")
@Timer
public class ReaderController {
    private final ReaderService readerService;

    @Autowired
    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    //    @RecoverException
    @GetMapping("/{id}")
    @Operation(summary = "Get reader by id", description = "Загружает читателя с указанным идентификатором в пути")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the reader",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reader.class))}),
            @ApiResponse(responseCode = "404", description = "Reader not found",
                    content = @Content)
    })
    public ResponseEntity<Reader> getByID(@PathVariable Long id) {
        Reader reader = readerService.getByID(id);
        return new ResponseEntity<>(reader, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all readers", description = "Загружает список всех читателей, которые зарегистрированы в библиотеке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all readers",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reader.class))})
    })
    public ResponseEntity<List<Reader>> getAll() {
        return ResponseEntity.ok(readerService.getAll());
    }

    /**
     * Возвращает список выдач данного читателя с невозвращёнными книгами
     *
     * @param id - идентификатор читателя
     * @return - список выдач с книгами, которые на руках у читателя
     */
    @GetMapping("/{id}/issue")
    @Operation(summary = "Get all issues by reader id", description = "Загружает список выдач книг у читателя с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the list of issues by reader",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Issue.class))}),
            @ApiResponse(responseCode = "404", description = "Reader not found",
                    content = @Content)
    })
    public ResponseEntity<List<Issue>> getIssuesByReaderID(@PathVariable("id") Long id) {
        return new ResponseEntity<>(readerService.getIssues(id), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create new reader", description = "Создаёт нового читателя в библиотеке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Create new reader",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reader.class))})
    })
    public ResponseEntity<Reader> create(@RequestBody Reader reader) {
        return new ResponseEntity<>(readerService.create(reader), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reader by id", description = "Обновляет имя читателя с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the reader",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reader.class))}),
            @ApiResponse(responseCode = "404", description = "Reader for update not found",
                    content = @Content)
    })
    public ResponseEntity<Reader> update(@PathVariable("id") Long id, @RequestBody Reader reader) {
        Reader updatedReader = readerService.update(id, reader);
        return new ResponseEntity<>(updatedReader, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reader by id", description = "Удаляет читателя с указанным идентификатором из библиотеки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the reader",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reader for deletion not found",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        readerService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Метод для обработки исключения в случае некорректно указанного ID читателя
     *
     * @return ответ со статусом 404
     */
    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> processNotFoundEntityException(NotFoundEntityException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
