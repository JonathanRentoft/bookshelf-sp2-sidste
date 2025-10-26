package dat.config;

import dat.entities.Hotel;
import dat.entities.Room;
import dat.security.entity.Role;
import dat.security.entity.User;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.Set;

public class Populate {

    public static void populateDatabase() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("hotel");

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Clear existing data using ENTITY names
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            // The line below was causing the error and has been removed, as Hibernate manages the join table.
            // em.createQuery("DELETE FROM user_roles").executeUpdate(); // <--- INCORRECT JPQL
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();

            // Create Hotels and Rooms
            Hotel california = new Hotel("Hotel California", "California", Hotel.HotelType.LUXURY);
            Hotel hilton = new Hotel("Hilton", "Copenhagen", Hotel.HotelType.STANDARD);

            Set<Room> calRooms = getCalRooms();
            california.setRooms(calRooms);

            Set<Room> hilRooms = getHilRooms();
            hilton.setRooms(hilRooms);

            em.persist(california);
            em.persist(hilton);

            // Create Roles
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            em.persist(userRole);
            em.persist(adminRole);

            // Create Users
            User user = new User("user", "user123");
            user.addRole(userRole);
            em.persist(user);

            User admin = new User("admin", "admin123");
            admin.addRole(adminRole);
            em.persist(admin);

            em.getTransaction().commit();
        }
    }

    private static Set<Room> getCalRooms() {
        Room r100 = new Room(100, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r101 = new Room(101, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r102 = new Room(102, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r103 = new Room(103, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r104 = new Room(104, new BigDecimal("3200"), Room.RoomType.DOUBLE);
        Room r105 = new Room(105, new BigDecimal("4500"), Room.RoomType.SUITE);
        return Set.of(r100, r101, r102, r103, r104, r105);
    }

    private static Set<Room> getHilRooms() {
        Room r111 = new Room(111, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r112 = new Room(112, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r113 = new Room(113, new BigDecimal("2520"), Room.RoomType.SINGLE);
        Room r114 = new Room(114, new BigDecimal("2520"), Room.RoomType.DOUBLE);
        Room r115 = new Room(115, new BigDecimal("3200"), Room.RoomType.DOUBLE);
        Room r116 = new Room(116, new BigDecimal("4500"), Room.RoomType.SUITE);
        return Set.of(r111, r112, r113, r114, r115, r116);
    }
}