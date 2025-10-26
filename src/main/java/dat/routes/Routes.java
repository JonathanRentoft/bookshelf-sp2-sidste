package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final BookRoute bookRoute = new BookRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/books", bookRoute.getRoutes());
        };
    }
}