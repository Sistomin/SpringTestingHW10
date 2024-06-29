package ru.gb.SpringTesting.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Класс, описывающий факт выдачи книги
 */
@Schema(name = "Факты выдачи книги читателю")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Issue {

    @Schema(name = "Идентификатор выдачи")
    private Long id;

    @Schema(name = "Идентификатор книги")
    private Long bookId;

    @Schema(name = "Идентификатор читателя")
    private Long readerId;

    @Schema(name = "Дата выдачи")
    private LocalDate issuedAt;

    @Schema(name = "Дата возврата")
    private LocalDate returnedAt;

    public Issue(Long bookId, Long readerId) {
        this.bookId = bookId;
        this.readerId = readerId;
    }
}
