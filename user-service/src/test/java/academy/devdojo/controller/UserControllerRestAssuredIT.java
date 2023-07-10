package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerRestAssuredIT extends IntegrationTestContainers {
    private static final String URL = "/v1/users";
    @Autowired
    private FileUtils fileUtils;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("findAll() returns a list with all users")
    @Order(1)
    @Sql(value = "/sql/user/init_three_users.sql")
    void findAll_ReturnsAllUsers_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/get-all-users-200.json");

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions
                .assertThatJson(response)
                .and(users -> {
                    users.node("[0].id").isNotNull();
                    users.node("[1].id").isNotNull();
                    users.node("[2].id").isNotNull();
                });

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("findAll() returns an empty list when no users are found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNoUsersAreFound() throws Exception {
        var response = fileUtils.readResourceFile("user/get-all-users-empty-list-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("findById() returns a optional user when id exists")
    @Order(3)
    void findById_ReturnsOptionalAnime_WhenIdExists() throws Exception {
        var id = 1L;
        var response = fileUtils.readResourceFile("user/get-user-by-id-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("findById() throw NotFoundException when no user is found")
    @Order(4)
    void findById_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

    }
}
