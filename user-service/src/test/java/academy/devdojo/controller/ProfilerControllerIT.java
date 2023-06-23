package academy.devdojo.controller;

import academy.devdojo.response.ProfileGetResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfilerControllerIT {
    private static final String URL = "/v1/profiles";
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("findAll() returns a list with all profiles")
    @Sql(value = "/sql/init_two_profiles.sql")
    void findAll_ReturnsAllProfiles_WhenSuccessful() throws Exception {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {};
        var response = testRestTemplate.exchange(URL, HttpMethod.GET, null, typeReference);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull().doesNotContainNull();
        response.getBody().forEach(profileGetResponse -> Assertions.assertThat(profileGetResponse).hasNoNullFieldsOrProperties());
    }
}
