package academy.devdojo.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@Lazy
public class TestRestTemplateConfig {
    @LocalServerPort
    int port;

    @Bean
    public TestRestTemplate testRestTemplate() {
        var uri = new DefaultUriBuilderFactory("http://localhost:" + port);
        var testRestTemplate = new TestRestTemplate()
                .withBasicAuth("mestre.kame@dbz.com", "test");
        testRestTemplate.setUriTemplateHandler(uri);
        return testRestTemplate;
    }
}
