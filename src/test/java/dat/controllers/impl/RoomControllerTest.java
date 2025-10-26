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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class RoomControllerTest {

    private static Javalin app;
    private static EntityManagerFactory emf;
    private static final String BASE_URL = "http://localhost:7777/api/v1";
    private Hotel h1;
    private Room r1;

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

            h1 = new Hotel("Test Hotel", "Test Vej 1", Hotel.HotelType.STANDARD);
            r1 = new Room(101, new BigDecimal("1200"), Room.RoomType.SINGLE);
            h1.addRoom(r1);

            em.persist(h1);
            em.getTransaction().commit();
        }
    }

    @Test
    void getAllRooms() {
        given()
                .when()
                .get("/rooms")
                .then()
                .statusCode(200)
                .body("", hasSize(1));
    }

    @Test
    void getRoomById() {
        given()
                .when()
                .get("/rooms/{id}", r1.getRoomId())
                .then()
                .statusCode(200)
                .body("roomNumber", equalTo(r1.getRoomNumber()))
                .body("roomPrice", equalTo(1200)); // Pris som Integer
    }

    @Test
    void createRoom() {
        Map<String, Object> newRoom = new HashMap<>();
        newRoom.put("roomNumber", 202);
        newRoom.put("roomPrice", 2500); // Pris som Integer
        newRoom.put("roomType", "SUITE");

        given()
                .contentType("application/json")
                .body(newRoom)
                .when()
                .post("/rooms/hotel/{id}", h1.getId())
                .then()
                .statusCode(201)
                .body("rooms", hasSize(2))
                .body("hotelName", equalTo(h1.getHotelName()));
    }

    @Test
    void updateRoom() {
        Map<String, Object> updatedRoom = new HashMap<>();
        updatedRoom.put("roomNumber", r1.getRoomNumber());
        updatedRoom.put("roomPrice", 9999); // Ny pris som Integer
        updatedRoom.put("roomType", r1.getRoomType().toString());

        given()
                .contentType("application/json")
                .body(updatedRoom)
                .when()
                .put("/rooms/{id}", r1.getRoomId())
                .then()
                .statusCode(200)
                .body("roomPrice", equalTo(9999));
    }

    @Test
    void deleteRoom() {
        given()
                .when()
                .delete("/rooms/{id}", r1.getRoomId())
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/rooms")
                .then()
                .statusCode(200)
                .body("", hasSize(0));
    }
}