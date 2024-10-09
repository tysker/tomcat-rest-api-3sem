package dk.backend.utilty;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.WebApplicationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EMF_Creator {
    public static void startFacadeWithTestDb() {
        System.setProperty("IS_INTEGRATION_TEST_WITH_DB", "testing");
    }

    public static void endFacadeWithTestDb() {
        System.clearProperty("IS_INTEGRATION_TEST_WITH_DB");
    }

    public static EntityManagerFactory createEntityManagerFactory() {
        return createEntityManagerFactory(false);
    }

    public static EntityManagerFactory createEntityManagerFactoryForTest() {
        return createEntityManagerFactory(true);
    }

    public static Dotenv dotenv = Dotenv.configure().load();

    private static EntityManagerFactory createEntityManagerFactory(boolean isTest) {

        boolean isDeployed = (System.getenv("DEPLOYED") != null);

        Properties props = new Properties();
        String DB_USERNAME = dotenv.get("DB_USERNAME");
        String DB_PASSWORD = dotenv.get("DB_PASSWORD");

        if(isDeployed) {
            String DB_NAME = getDBName();
            String CONNECTION_STR = dotenv.get("CONNECTION_STR") + DB_NAME;

            props.setProperty("jakarta.persistence.jdbc.user", DB_USERNAME);
            props.setProperty("jakarta.persistence.jdbc.password", DB_PASSWORD);
            props.setProperty("jakarta.persistence.jdbc.url", CONNECTION_STR);
            return Persistence.createEntityManagerFactory("pu", props);
        }

        String puName = isTest || System.getProperty("IS_INTEGRATION_TEST_WITH_DB") != null ? "pu-test" : "pu";

        if (puName.equals("pu-test")) {
            System.out.println("Using the TEST database via persistence-unit --> pu-test ");
        } else {
            props.setProperty("jakarta.persistence.jdbc.user", DB_USERNAME);
            props.setProperty("jakarta.persistence.jdbc.password", DB_PASSWORD);
            System.out.println("Using the DEV database via persistence-unit --> pu ");
            return Persistence.createEntityManagerFactory("pu", props);
        }

        try {
            return Persistence.createEntityManagerFactory(puName, null);
        } catch (Exception ex) {
            System.out.println("Error creating EntityManagerFactory: " + ex.getMessage());
            System.out.println("END OF STACKTRACE");
            throw new WebApplicationException("Internal Server Error. We are sorry for the inconvenience", 500);
        }
    }

    private static String getDBName() {
        Properties pomProperties;
        InputStream is = EMF_Creator.class.getClassLoader().getResourceAsStream("properties-from-pom.properties");
        pomProperties = new Properties();
        try {
            pomProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pomProperties.getProperty("db.name");
    }

}
