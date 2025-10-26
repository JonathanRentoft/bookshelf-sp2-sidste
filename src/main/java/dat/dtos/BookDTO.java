package dat.dtos;

import dat.entities.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BookDTO {

    private Integer id;
    private String title;
    private String author;
    private String publisher;
    private Integer yearPublished;
    private Book.Genre genre;

    public BookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.yearPublished = book.getYearPublished();
        this.genre = book.getGenre();
    }

    public BookDTO(String title, String author, String publisher, Integer yearPublished, Book.Genre genre) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
        this.genre = genre;
    }

    public static List<BookDTO> toBookDTOList(List<Book> books) {
        return books.stream().map(BookDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookDTO bookDto)) return false;

        return getId().equals(bookDto.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}