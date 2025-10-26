package dat.security.dao;

import dat.config.HibernateConfig;
import dat.security.entity.Role;
import dat.security.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserDAO {

    private static UserDAO instance;
    private static EntityManagerFactory emf;

    public static UserDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserDAO();
        }
        return instance;
    }

    public User getVerifiedUser(String username, String password) throws Exception {
        try (EntityManager em = emf.createEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new Exception("Invalid user name or password");
            }
            return user;
        }
    }

    public User createUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User user = new User(username, password);
            Role userRole = em.find(Role.class, "user");
            if (userRole == null) {
                userRole = new Role("user");
                em.persist(userRole);
            }
            user.addRole(userRole);
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }
}