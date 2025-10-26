package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.RoomDTO;
import dat.entities.Hotel;
import dat.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomDAOTest {

    private static EntityManagerFactory emf;
    private static RoomDAO roomDAO;
    private Hotel h1;
    private Room r1, r2;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        roomDAO = RoomDAO.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();

            h1 = new Hotel("Test Hotel", "Test Vej 1", Hotel.HotelType.STANDARD);
            r1 = new Room(101, new BigDecimal("1000"), Room.RoomType.SINGLE);
            r2 = new Room(102, new BigDecimal("1500"), Room.RoomType.DOUBLE);
            h1.addRoom(r1);
            h1.addRoom(r2);

            em.persist(h1);
            em.getTransaction().commit();
        }
    }

    @Test
    void read() {
        RoomDTO found = roomDAO.read(r1.getRoomId());
        assertNotNull(found);
        assertEquals(r1.getRoomNumber(), found.getRoomNumber());
    }

    @Test
    void readAll() {
        List<RoomDTO> allRooms = roomDAO.readAll();
        assertEquals(2, allRooms.size());
    }

    @Test
    void update() {
        // Opret en DTO baseret på det eksisterende rum
        RoomDTO roomToUpdate = new RoomDTO(r1);
        // Brug setter til at ændre prisen (som er Integer i DTO'en)
        roomToUpdate.setRoomPrice(9999);

        // Kald update-metoden i DAO'en
        RoomDTO updatedRoom = roomDAO.update(r1.getRoomId(), roomToUpdate);

        // Verificer at prisen er blevet opdateret
        assertEquals(9999, updatedRoom.getRoomPrice());
    }

    @Test
    void delete() {
        roomDAO.delete(r1.getRoomId());
        List<RoomDTO> allRooms = roomDAO.readAll();
        assertEquals(1, allRooms.size());
        assertNull(roomDAO.read(r1.getRoomId()));
    }

    @Test
    void addRoomToHotel() {
        RoomDTO newRoomDTO = new RoomDTO();
        newRoomDTO.setRoomNumber(103);
        newRoomDTO.setRoomType(Room.RoomType.SUITE);
        newRoomDTO.setRoomPrice(3000); // Pris som Integer

        roomDAO.addRoomToHotel(h1.getId(), newRoomDTO);
        assertEquals(3, roomDAO.readAll().size());
    }
}