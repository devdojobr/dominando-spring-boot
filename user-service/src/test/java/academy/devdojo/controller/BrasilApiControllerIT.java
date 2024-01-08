package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import academy.devdojo.config.RestAssuredConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestAssuredConfig.class)
@Sql(value = "/sql/user/init_one_login_regular_user.sql")
@AutoConfigureWireMock(port = 0, files = "classpath:/wiremock/brasil-api", stubs = "classpath:/wiremock/brasil-api/mappings")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrasilApiControllerIT extends IntegrationTestContainers {
    private static final String URL = "/v1/brasil-api/cep";
    @Autowired
    private FileUtils fileUtils;

    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;

    @BeforeEach
    void setUrl() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Order(1)
    @Test
    @DisplayName("findCep returns CepGetResponse when successful")
    void findCep_ReturnsCepGetResponse_WhenSuccessful() throws Exception {
        var cep = "00000";
        var expectedResponse = fileUtils.readResourceFile("brasil-api/expected-get-cep-response-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{cep}", cep)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(expectedResponse));
    }

    @Order(2)
    @Test
    @DisplayName("findCep returns CepErrorResponse when unsuccessful")
    void findCep_ReturnsCepErrorResponse_WhenUnsuccessful() throws Exception {
        var cep = "40400";
        var expectedResponse = fileUtils.readResourceFile("brasil-api/expected-get-cep-response-404.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{cep}", cep)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse));
    }
}
