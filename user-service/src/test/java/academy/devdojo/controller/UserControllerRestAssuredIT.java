package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import academy.devdojo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.assertj.core.api.Assertions;
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
    @Autowired
    private UserRepository repository;

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
                .log().all()
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("findById() returns a optional user when id exists")
    @Order(3)
    @Sql("/sql/user/init_one_user.sql")
    void findById_ReturnsOptionalAnime_WhenIdExists() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/get-user-by-id-200.json");
        var users = repository.findAll();

        Assertions.assertThat(users).hasSize(1);

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL + "/" + users.get(0).getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("id")
                .asNumber()
                .isPositive();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("findById() throw NotFoundException when no user is found")
    @Order(4)
    void findById_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .get(URL + "/92182")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();

    }
}

