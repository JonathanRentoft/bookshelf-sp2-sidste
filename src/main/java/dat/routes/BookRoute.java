package dat.routes;

import dat.controllers.impl.BookController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class BookRoute {

    private final BookController bookController = new BookController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", bookController::create);
            get("/", bookController::readAll);
            get("/{id}", bookController::read);
            put("/{id}", bookController::update);
            delete("/{id}", bookController::delete);
        };
    }
}