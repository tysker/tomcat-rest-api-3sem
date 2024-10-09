package dk.backend.utility;

import dk.backend.entities.Role;
import dk.backend.entities.User;
import jakarta.persistence.EntityManager;

public class CreateTestData {
    public static void createTestData(EntityManager em) {

        User user = new User("usertest", "user123");
        User admin = new User("admintest", "admin123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user.addRole(userRole);
        admin.addRole(adminRole);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}