package dat.daos.impl;

import dat.dtos.HotelDTO;
import dat.entities.Hotel;
import dat.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dat.config.HibernateConfig.getEntityManagerFactoryForTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HotelDAOTest {
    private static HotelDAO hotelDAO;
    private static EntityManagerFactory emf;
    private static EntityManager em;


    @BeforeAll
    static void beforeALl() {
        emf = getEntityManagerFactoryForTest();
        hotelDAO = HotelDAO.getInstance(emf);
    }


    @BeforeEach
    public void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();

            // Create valid test data
            Hotel hotel1 = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);
            Hotel hotel2 = new Hotel("Hilton", "Copenhagen", Hotel.HotelType.STANDARD);

            em.persist(hotel1);
            em.persist(hotel2);
            em.getTransaction().commit();
        }
    }

    @Test
    void readAll() {
        List<HotelDTO> hotels = hotelDAO.readAll();
        assertEquals(2, hotels.size());
    }






}
