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
import ru.gb.SpringTesting.exception.UserIssueLimitExceededException;
import ru.gb.SpringTesting.model.Issue;
import ru.gb.SpringTesting.service.IssueService;

import java.util.List;

@RestController
@RequestMapping("/issue")
@Tag(name = "Book issues")
@Timer
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get issue by id", description = "Загружает факт выдачи с указанным идентификатором в пути")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the issue",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Issue.class))}),
            @ApiResponse(responseCode = "404", description = "Issue not found",
                    content = @Content)
    })
    public ResponseEntity<Issue> getByID(@PathVariable("id") Long id) {
        Issue issue = issueService.getByID(id);
        return new ResponseEntity<>(issue, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all issues", description = "Загружает список всех выдач книг читателям")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all issues",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Issue.class))})
    })
    public ResponseEntity<List<Issue>> getAll() {
        return new ResponseEntity<>(issueService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create new issue", description = "Создаёт новую выдачу книги читателю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Create new issue",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Issue.class))}),
            @ApiResponse(responseCode = "404", description = "Book or reader with selected id not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Reader exceeded the permissible number of books issued",
                    content = @Content),
    })
    public ResponseEntity<Issue> create(@RequestBody Issue issue) {
        issue = issueService.saveIssue(issue);
        return new ResponseEntity<>(issue, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Return book by issue id", description = "Оформляет возврат книги с указанным идентификатором выдачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the book by issue id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Issue.class))}),
            @ApiResponse(responseCode = "404", description = "Book for returning not found",
                    content = @Content)
    })
    public ResponseEntity<Issue> update(@PathVariable("id") Long id) {
        Issue updatedIssue = issueService.update(id);
        return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete issue by id", description = "Удаляет факт выдачи с указанным идентификатором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete the issue",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Issue for deletion not found",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        issueService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Метод для обработки исключений в случае некорректно указанного ID книги, читателя или выдачи
     *
     * @return ответ со статусом 404
     */
    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> processNotFoundEntityException(NotFoundEntityException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Метод для обработки исключений в случае превышения количества выдач книг для одного читателя
     *
     * @return ответ со статусом 403
     */
    @ExceptionHandler(UserIssueLimitExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> processUserIssueLimitExceededException(UserIssueLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
