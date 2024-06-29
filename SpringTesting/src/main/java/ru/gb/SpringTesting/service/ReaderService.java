package ru.gb.SpringTesting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.SpringTesting.entity.ReaderEntity;
import ru.gb.SpringTesting.exception.NotFoundEntityException;
import ru.gb.SpringTesting.model.Issue;
import ru.gb.SpringTesting.model.Reader;
import ru.gb.SpringTesting.repository.IssueRepository;
import ru.gb.SpringTesting.repository.ReaderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final IssueRepository issueRepository;

    // Метод для сопоставления и перевода ReaderEntity в Reader
    static Reader mapping(ReaderEntity readerEntity) {
        return new Reader(readerEntity.getId(), readerEntity.getName());
    }

    // Метод для сопоставления и перевода Reader в ReaderEntity
    static ReaderEntity mapping(Reader reader) {
        return new ReaderEntity(reader.getId(), reader.getName());
    }

    public Reader getByID(Long id) {
        return mapping(readerRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Reader with id = " + id + " not found")));
    }

    public List<Reader> getAll() {
        return readerRepository.findAll().stream()
                .map(ReaderService::mapping)
                .toList();
    }

    // Для получения списка выдач книг читателю, которые ещё не сданы,
    // используем метод, реализованный в репозитории выдач книг IssueRepository
    public List<Issue> getIssues(Long readerId) {
        // Проверяем корректность указания id (будет выброшено исключение, если некорректный идентификатор)
        getByID(readerId);
        return issueRepository.findByReaderIdAndReturnedAt(readerId, null).stream()
                .map(IssueService::mapping).toList();
    }

    public Reader create(Reader reader) {
        return mapping(readerRepository.save(mapping(reader)));
    }

    public Reader update(Long id, Reader reader) {
        Reader updatedReader = getByID(id);
        updatedReader.setName(reader.getName());
        return mapping(readerRepository.save(mapping(updatedReader)));
    }

    public void deleteById(Long id) {
        getByID(id);
        readerRepository.deleteById(id);
    }
}
