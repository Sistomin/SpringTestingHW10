package ru.gb.SpringTesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.SpringTesting.entity.BookEntity;

/**
 * Репозиторий для взаимодействия с хранилищем книг в базе данных Н2
 */
public interface BookRepository extends JpaRepository<BookEntity, Long> {

}
