package ru.gb.SpringTesting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.gb.SpringTesting.entity.BookEntity;
import ru.gb.SpringTesting.model.Book;
import ru.gb.SpringTesting.repository.BookRepository;
import ru.gb.SpringTesting.service.BookService;

import java.util.List;
import java.util.Objects;

class BookControllerTest extends JUnitSpringBootBase {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    BookService bookService;
    @Autowired
    BookRepository bookRepository;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class JUnitBook {
        private Long id;
        private String name;

        public JUnitBook(String name) {
            this.name = name;
        }
    }

    @Test
    @DisplayName("GET /book/id - получение книги с существующим ID")
    void testGetByIdSuccess() {
        Book expected = bookService.create(new Book("Spring in action"));

        JUnitBook responseBody = webTestClient.get()
                .uri("/book/" + expected.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(JUnitBook.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(expected.getId(), responseBody.getId());
        Assertions.assertEquals(expected.getName(), responseBody.getName());
    }

    @Test
    @DisplayName("GET /book/id - получение книги с НЕ существующим ID")
    void testGetByIdNotFound() {
        webTestClient.get()
                .uri("/book/" + Long.MAX_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /book - получение списка ВСЕХ книг")
    void testGetAll() {
        bookRepository.saveAll(List.of(
                new BookEntity("Java is simple"),
                new BookEntity("Kotlin in action")
        ));
        List<Book> expected = bookService.getAll();

        List<JUnitBook> responseBody = webTestClient.get()
                .uri("/book")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<JUnitBook>>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(expected.size(), responseBody.size());
        for (JUnitBook book : responseBody) {
            boolean found = expected.stream()
                    .filter(it -> Objects.equals(it.getId(), book.getId()))
                    .anyMatch(it -> Objects.equals(it.getName(), book.getName()));
            Assertions.assertTrue(found);
        }
    }

    @Test
    @DisplayName("POST /book - создание новой книги")
    void testCreateBook() {
        JUnitBook createdBook = new JUnitBook("The catcher in the rye");

        JUnitBook responseBody = webTestClient.post()
                .uri("/book")
                .bodyValue(createdBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(JUnitBook.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.getId());
        Assertions.assertEquals(createdBook.getName(), responseBody.getName());
        Assertions.assertTrue(bookRepository.findById(responseBody.getId()).isPresent());
    }

    @Test
    @DisplayName("PUT /book/id - обновление существующей книги")
    void testUpdateById() {
        Book updatedBook = bookService.create(new Book("Spring Boot is the best!"));
        JUnitBook requestForUpdate = new JUnitBook("Spring Boot 3.0");

        JUnitBook responseBody = webTestClient.put()
                .uri("/book/" + updatedBook.getId())
                .bodyValue(requestForUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JUnitBook.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(updatedBook.getId(), responseBody.getId());
        Assertions.assertEquals(requestForUpdate.getName(), responseBody.getName());
    }

    @Test
    @DisplayName("DELETE /book/id - удаление существующей книги")
    void testDeleteById() {
        Book deletedBook = bookService.create(new Book("Spring in action"));

        webTestClient.delete()
                .uri("/book/" + deletedBook.getId())
                .exchange()
                .expectStatus().isNoContent();

        // Проверяем также, что книги с данным Id нет в БД.
        Assertions.assertFalse(bookRepository.findById(deletedBook.getId()).isPresent());
    }
}
