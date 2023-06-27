package academy.devdojo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class MyTestContainersConfiguration {

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySqlContainer() {
        return new MySQLContainer<>("mysql:8.0.33");
    }

}