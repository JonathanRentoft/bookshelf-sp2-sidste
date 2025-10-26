package dat.entities;

import dat.dtos.BookDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "author", nullable = false)
    private String author;

    @Setter
    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Setter
    @Column(name = "year_published", nullable = false)
    private Integer yearPublished;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

    public Book(String title, String author, String publisher, Integer yearPublished, Genre genre) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
        this.genre = genre;
    }

    public Book(BookDTO bookDTO) {
        this.id = bookDTO.getId();
        this.title = bookDTO.getTitle();
        this.author = bookDTO.getAuthor();
        this.publisher = bookDTO.getPublisher();
        this.yearPublished = bookDTO.getYearPublished();
        this.genre = bookDTO.getGenre();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }

    public enum Genre {
        FICTION, NONFICTION, SCIENCE, HISTORY, BIOGRAPHY, FANTASY, MYSTERY, ROMANCE, THRILLER, HORROR
    }
}
