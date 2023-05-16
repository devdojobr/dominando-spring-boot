package academy.devdojo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import test.outside.Connection;

@Configuration
public class BeanConfig {
    @Value("${database.url}")
    private String url;
    @Value("${database.username}")
    private String username;
    @Value("${database.password}")
    private String password;
    @Bean
//    @Primary
    public Connection connectionMySql(){
        return new Connection(url, username, password);
    }

    @Bean(name = "mongoDB")
    public Connection connectionMongoDb(){
        return new Connection("localhost", "mongodb", "xxxx");
    }
}
