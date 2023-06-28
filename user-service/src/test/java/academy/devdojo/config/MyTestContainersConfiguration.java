package academy.devdojo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

import static academy.devdojo.config.IntegrationTestContainers.MY_SQL_CONTAINER;

@TestConfiguration(proxyBeanMethods = false)
@ImportTestcontainers(IntegrationTestContainers.class)
public class MyTestContainersConfiguration {

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySqlContainer() {
        return MY_SQL_CONTAINER;
    }

}