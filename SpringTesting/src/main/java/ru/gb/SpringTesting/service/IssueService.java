package ru.gb.SpringTesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.gb.SpringTesting.entity.IssueEntity;
import ru.gb.SpringTesting.exception.NotFoundEntityException;
import ru.gb.SpringTesting.exception.UserIssueLimitExceededException;
import ru.gb.SpringTesting.model.Issue;
import ru.gb.SpringTesting.repository.BookRepository;
import ru.gb.SpringTesting.repository.IssueRepository;
import ru.gb.SpringTesting.repository.ReaderRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final IssueRepository issueRepository;

    // Переменная для хранения максимального количества книг для одного читателя, которое указано в файле настроек
    // Если параметр не задан - то используется значение 1.
    @Value("${application.max-allowed-books:1}")
    private int maxAllowedBooks;

    // Метод для сопоставления и перевода IssueEntity в Issue
    static Issue mapping(IssueEntity issueEntity) {
        return new Issue(issueEntity.getId(), issueEntity.getBookId(), issueEntity.getReaderId(),
                issueEntity.getIssuedAt(), issueEntity.getReturnedAt());
    }

    // Метод для сопоставления и перевода Issue в IssueEntity
    static IssueEntity mapping(Issue issue) {
        return new IssueEntity(issue.getId(), issue.getBookId(), issue.getReaderId(),
                issue.getIssuedAt(), issue.getReturnedAt());
    }

    public Issue getByID(Long id) {
        return mapping(issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Issue with id = " + id + " not found")));
    }

    public List<Issue> getAll() {
        return issueRepository.findAll().stream()
                .map(IssueService::mapping)
                .toList();
    }

    // Перед созданием и сохранением в хранилище факта выдачи книги читателю делаем проверку валидности идентификаторов
    // книги и читателя, а также проверку на соблюдение ограничения на количество выданных читателю книг.
    public Issue saveIssue(Issue issue) {
        bookRepository.findById(issue.getBookId())
                .orElseThrow(() -> new NotFoundEntityException("Book not found with id = " + issue.getBookId()));
        readerRepository.findById(issue.getReaderId())
                .orElseThrow(() -> new NotFoundEntityException("Reader not found with id = " + issue.getReaderId()));

        if (maxAllowedBooks <= issueRepository.findByReaderIdAndReturnedAt(issue.getReaderId(), null).size()) {
            throw new UserIssueLimitExceededException("Читателю с ID = " + issue.getReaderId() +
                    " отказано в выдаче по причине превышения максимального количества книг на руках.");
        }
        // Оформляем выдачу только после прохождения всех проверок
        issue.setIssuedAt(LocalDate.now()); // устанавливаем дату выдачи книги
        return mapping(issueRepository.save(mapping(issue)));
    }

    public Issue update(Long id) {
        Issue updatedIssue = getByID(id);
        updatedIssue.setReturnedAt(LocalDate.now());
        return mapping(issueRepository.save(mapping(updatedIssue)));
    }

    public void deleteById(Long id) {
        getByID(id);
        issueRepository.deleteById(id);
    }
}
