package ru.gb.SpringTesting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Класс, описывающий факт выдачи книги для работы с базой данных
 */

@Entity
@Table(name = "issues")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "reader_id", nullable = false)
    private Long readerId;

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "returned_at")
    private LocalDate returnedAt;

    public IssueEntity(Long bookId, Long readerId) {
        this.bookId = bookId;
        this.readerId = readerId;
    }

    public IssueEntity(Long bookId, Long readerId, LocalDate issuedAt) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.issuedAt = issuedAt;
    }
}
