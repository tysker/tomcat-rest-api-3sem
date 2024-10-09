package dk.backend.integration;

import dk.backend.exceptions.API_Exception;
import dk.backend.exceptions.authentication.AuthenticationException;
import dk.backend.facade.user.UserFacade;
import dk.backend.utility.CreateTestData;
import dk.backend.utilty.EMF_Creator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserFacadeTest {

    private static UserFacade USER_FACADE;

    @BeforeAll
    public static void setupClass() {
        EMF_Creator.startFacadeWithTestDb();
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactoryForTest();
        USER_FACADE = UserFacade.getUserFacade(emf);
        CreateTestData.createTestData(emf.createEntityManager());
    }

    @AfterAll
    public static void tearDownClass() {
        EMF_Creator.endFacadeWithTestDb();
    }

    @Test
    @DisplayName("Verify if user passes authentication")
    void getVerifiedUser() throws AuthenticationException {
        var user = USER_FACADE.getVerifiedUser("usertest", "user123");
        if(user != null) {
            assertTrue(true);
        } else {
            fail();
        }
    }

    @Test
    @DisplayName("Create a test user")
    void createUser() throws API_Exception {
        var user = USER_FACADE.createUser("test", "test123", "user");
        assertEquals("test", user.getUserName());
        System.out.println(user.getRoles().toString());

    }

    @Test
    @DisplayName("Create a test role")
    void createRole() {
        var role = USER_FACADE.createRole("test");
        assertEquals("test", role.getRoleName());
    }
}