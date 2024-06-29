package ru.gb.SpringTesting.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.SpringTesting.aspect.Timer;
import ru.gb.SpringTesting.model.Book;
import ru.gb.SpringTesting.model.Issue;
import ru.gb.SpringTesting.model.Reader;
import ru.gb.SpringTesting.service.BookService;
import ru.gb.SpringTesting.service.IssueService;
import ru.gb.SpringTesting.service.ReaderService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ui")
@Timer
public class UIController {
    private final BookService bookService;
    private final ReaderService readerService;
    private final IssueService issueService;

    @Autowired
    public UIController(BookService bookService, ReaderService readerService, IssueService issueService) {
        this.bookService = bookService;
        this.readerService = readerService;
        this.issueService = issueService;
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {
        List<Book> listBooks = bookService.getAll();
        model.addAttribute("books", listBooks);
        return "books";
    }

    @GetMapping("/books/{id}")
    public String getDescriptionBook(@PathVariable("id") Long id, Model model) {
        model.addAttribute("bookById", bookService.getByID(id));
        return "bookById";
    }

    @GetMapping("/readers")
    public String getAllReaders(Model model) {
        List<Reader> listReaders = readerService.getAll();
        model.addAttribute("readers", listReaders);
        return "readers";
    }

    @GetMapping("/readers/{id}")
    public String getDescriptionReader(@PathVariable("id") Long id, Model model) {
        model.addAttribute("readerById", readerService.getByID(id));
        return "readerById";
    }

    @GetMapping("/issues")
    public String getAllIssues(Model model) {
        List<Issue> listIssues = issueService.getAll();
        model.addAttribute("issues", listIssues);
        return "issues";
    }

    @GetMapping("/reader/{id}")
    public String getBooksByReaderId(@PathVariable("id") Long id, Model model) {
        List<Issue> listIssues = readerService.getIssues(id);
        Reader reader = readerService.getByID(id);
        String readerInfo = String.format("%s (id = %d)", reader.getName(), reader.getId());
        model.addAllAttributes(Map.of("readerInfo", readerInfo, "issues", listIssues));
        return "readerIssues";
    }
}
