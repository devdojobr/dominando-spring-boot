package academy.devdojo.config;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface IntegrationTestContainers {
    @Container
    MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.33");
}
