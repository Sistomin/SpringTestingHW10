package ru.gb.SpringTesting.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, описывающий книгу
 */
@Schema(name = "Книга")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Schema(name = "Идентификатор")
    private Long id;

    @Schema(name = "Наименование книги")
    private String name;

    public Book(String name) {
        this.name = name;
    }
}
