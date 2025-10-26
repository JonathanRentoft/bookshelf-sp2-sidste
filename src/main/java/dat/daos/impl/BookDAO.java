package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.BookDTO;
import dat.entities.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BookDAO implements IDAO<BookDTO, Integer> {

    private static BookDAO instance;
    private static EntityManagerFactory emf;

    public static BookDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new BookDAO();
        }
        return instance;
    }

    @Override
    public BookDTO read(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Book book = em.find(Book.class, integer);
            return new BookDTO(book);
        }
    }

    @Override
    public List<BookDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BookDTO> query = em.createQuery("SELECT new dat.dtos.BookDTO(b) FROM Book b", BookDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public BookDTO create(BookDTO bookDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Book book = new Book(bookDTO);
            em.persist(book);
            em.getTransaction().commit();
            return new BookDTO(book);
        }
    }

    @Override
    public BookDTO update(Integer integer, BookDTO bookDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Book b = em.find(Book.class, integer);
            b.setTitle(bookDTO.getTitle());
            b.setAuthor(bookDTO.getAuthor());
            b.setPublisher(bookDTO.getPublisher());
            b.setYearPublished(bookDTO.getYearPublished());
            b.setGenre(bookDTO.getGenre());
            Book mergedBook = em.merge(b);
            em.getTransaction().commit();
            return mergedBook != null ? new BookDTO(mergedBook) : null;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Book book = em.find(Book.class, integer);
            if (book != null) {
                em.remove(book);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Book book = em.find(Book.class, integer);
            return book != null;
        }
    }
}
