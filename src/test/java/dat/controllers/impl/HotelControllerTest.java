package dat.controllers.impl;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.entities.Hotel;
import dat.entities.Room;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class HotelControllerTest {

    private static Javalin app;
    private static EntityManagerFactory emf;
    private static final String BASE_URL = "http://localhost:7777/api/v1";
    private Hotel h1, h2;

    @BeforeAll
    static void beforeAll() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        app = ApplicationConfig.startServer(7777);
        RestAssured.baseURI = BASE_URL;
    }

    @AfterAll
    static void afterAll() {
        ApplicationConfig.stopServer(app);
        HibernateConfig.setTest(false);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();

            h1 = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);
            h2 = new Hotel("Hilton", "Copenhagen", Hotel.HotelType.STANDARD);
            Room r1 = new Room(101, new BigDecimal("2500"), Room.RoomType.SINGLE);
            h1.addRoom(r1);

            em.persist(h1);
            em.persist(h2);
            em.getTransaction().commit();
        }
    }

    @Test
    void testGetAllHotels() {
        given()
                .when()
                .get("/hotels")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].hotelName", equalTo(h1.getHotelName()))
                .body("[1].hotelName", equalTo(h2.getHotelName()));
    }

    @Test
    void testGetHotelById() {
        given()
                .when()
                .get("/hotels/{id}", h1.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(h1.getId()))
                .body("hotelName", equalTo(h1.getHotelName()))
                .body("rooms.size()", equalTo(1));
    }

    @Test
    void testGetHotelByIdNotFound() {
        int nonExistentId = 9999;
        given()
                .when()
                .get("/hotels/{id}", nonExistentId)
                .then()
                // Din ExceptionHandler fanger sandsynligvis en fejl og returnerer 500
                .statusCode(500)
                .body("message", containsString("Internal server error"));
    }

    @Test
    void testPostCreateHotel() {
        Map<String, Object> newHotel = new HashMap<>();
        newHotel.put("hotelName", "Brand New Hotel");
        newHotel.put("hotelAddress", "Somewhere 123");
        newHotel.put("hotelType", "BUDGET");

        given()
                .contentType("application/json")
                .body(newHotel)
                .when()
                .post("/hotels")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("hotelName", equalTo("Brand New Hotel"))
                .body("hotelAddress", equalTo("Somewhere 123"));
    }

    @Test
    void testPutUpdateHotel() {
        Map<String, Object> updatedInfo = new HashMap<>();
        updatedInfo.put("hotelName", "An Updated Hotel Name");
        updatedInfo.put("hotelAddress", h2.getHotelAddress());
        updatedInfo.put("hotelType", h2.getHotelType().toString());

        given()
                .contentType("application/json")
                .body(updatedInfo)
                .when()
                .put("/hotels/{id}", h2.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(h2.getId()))
                .body("hotelName", equalTo("An Updated Hotel Name"));
    }

    @Test
    void testDeleteHotel() {
        given()
                .when()
                .delete("/hotels/{id}", h1.getId())
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/hotels")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(h2.getId()));
    }
}