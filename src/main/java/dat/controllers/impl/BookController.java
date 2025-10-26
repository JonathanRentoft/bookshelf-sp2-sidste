package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.BookDAO;
import dat.dtos.BookDTO;
import dat.entities.Book;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class BookController implements IController<BookDTO, Integer> {

    private final BookDAO dao;

    public BookController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("books_db");
        this.dao = BookDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx)  {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        BookDTO bookDTO = dao.read(id);
        // response
        ctx.res().setStatus(200);
        ctx.json(bookDTO, BookDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        // List of DTOS
        List<BookDTO> bookDTOS = dao.readAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(bookDTOS, BookDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        BookDTO jsonRequest = ctx.bodyAsClass(BookDTO.class);
        // DTO
        BookDTO bookDTO = dao.create(jsonRequest);
        // response
        ctx.res().setStatus(201);
        ctx.json(bookDTO, BookDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // dto
        BookDTO bookDTO = dao.update(id, validateEntity(ctx));
        // response
        ctx.res().setStatus(200);
        ctx.json(bookDTO, Book.class);
    }

    @Override
    public void delete(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        dao.delete(id);
        // response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public BookDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(BookDTO.class)
                .check( b -> b.getTitle() != null && !b.getTitle().isEmpty(), "Title must be set")
                .check( b -> b.getAuthor() != null && !b.getAuthor().isEmpty(), "Author must be set")
                .check( b -> b.getPublisher() != null && !b.getPublisher().isEmpty(), "Publisher must be set")
                .check( b -> b.getYearPublished() != null && b.getYearPublished() > 0, "Year published must be set")
                .check( b -> b.getGenre() != null, "Genre must be set")
                .get();
    }
}