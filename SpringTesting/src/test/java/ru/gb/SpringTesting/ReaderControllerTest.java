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
import ru.gb.SpringTesting.entity.ReaderEntity;
import ru.gb.SpringTesting.model.Reader;
import ru.gb.SpringTesting.repository.ReaderRepository;
import ru.gb.SpringTesting.service.ReaderService;

import java.util.List;
import java.util.Objects;

class ReaderControllerTest extends JUnitSpringBootBase {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReaderService readerService;
    @Autowired
    ReaderRepository readerRepository;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class JUnitReader {
        private Long id;
        private String name;

        public JUnitReader(String name) {
            this.name = name;
        }
    }

    @Test
    @DisplayName("GET /reader/id - получение читателя с существующим ID")
    void testGetByIdSuccess() {
        Reader expected = readerService.create(new Reader("Tretyakov Ivan"));

        JUnitReader responseBody = webTestClient.get()
                .uri("/reader/" + expected.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(JUnitReader.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(expected.getId(), responseBody.getId());
        Assertions.assertEquals(expected.getName(), responseBody.getName());
    }

    @Test
    @DisplayName("GET /reader/id - получение читателя с НЕ существующим ID")
    void testGetByIdNotFound() {
        webTestClient.get()
                .uri("/reader/" + Long.MAX_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /reader - получение списка ВСЕХ читателей")
    void testGetAll() {
        readerRepository.saveAll(List.of(
                new ReaderEntity("Oleg"),
                new ReaderEntity("Anna")
        ));
        List<Reader> expected = readerService.getAll();

        List<JUnitReader> responseBody = webTestClient.get()
                .uri("/reader")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<JUnitReader>>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(expected.size(), responseBody.size());
        for (JUnitReader reader : responseBody) {
            boolean found = expected.stream()
                    .filter(it -> Objects.equals(it.getId(), reader.getId()))
                    .anyMatch(it -> Objects.equals(it.getName(), reader.getName()));
            Assertions.assertTrue(found);
        }
    }

    @Test
    @DisplayName("POST /reader - создание нового читателя")
    void testCreateReader() {
        JUnitReader createdReader = new JUnitReader("Alex");

        JUnitReader responseBody = webTestClient.post()
                .uri("/reader")
                .bodyValue(createdReader)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(JUnitReader.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.getId());
        Assertions.assertEquals(createdReader.getName(), responseBody.getName());
        Assertions.assertTrue(readerRepository.findById(responseBody.getId()).isPresent());
    }

    @Test
    @DisplayName("PUT /reader/id - обновление существующего читателя")
    void testUpdateById() {
        Reader updatedReader = readerService.create(new Reader("John"));
        JUnitReader requestForUpdate = new JUnitReader("David");

        JUnitReader responseBody = webTestClient.put()
                .uri("/reader/" + updatedReader.getId())
                .bodyValue(requestForUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JUnitReader.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(updatedReader.getId(), responseBody.getId());
        Assertions.assertEquals(requestForUpdate.getName(), responseBody.getName());
    }

    @Test
    @DisplayName("DELETE /reader/id - удаление существующего читателя")
    void testDeleteById() {
        Reader deletedReader = readerService.create(new Reader("Mike"));

        webTestClient.delete()
                .uri("/reader/" + deletedReader.getId())
                .exchange()
                .expectStatus().isNoContent();

        // Проверяем также, что читателя с данным Id нет в БД.
        Assertions.assertFalse(readerRepository.findById(deletedReader.getId()).isPresent());
    }
}