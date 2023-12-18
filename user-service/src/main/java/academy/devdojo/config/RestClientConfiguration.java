package academy.devdojo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {
    private final BrasilApiConfigurationProperties brasilApiConfigurationProperties;

    @Bean
    public RestClient brasilApiRestClient(RestClient.Builder builder){
        return builder.baseUrl(brasilApiConfigurationProperties.baseUrl()).build();
    }
}
