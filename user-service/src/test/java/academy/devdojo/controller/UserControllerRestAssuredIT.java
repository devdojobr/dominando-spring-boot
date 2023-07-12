package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    @Test
    @DisplayName("save() creates user")
    @Order(4)
    void save_CreatesUser_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("user/post-request-user-200.json");
        var expectedResponse = fileUtils.readResourceFile("user/post-response-user-201.json");

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
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
    @DisplayName("delete() removes a user")
    @Order(5)
    @Sql("/sql/user/init_one_user.sql")
    void delete_RemovesUser_WhenSuccessful() throws Exception {
        var users = repository.findAll();
        Assertions.assertThat(users).hasSize(1);

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .delete(URL + "/" + users.get(0).getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    @Test
    @DisplayName("delete() throw NotFoundException when no user is found")
    @Order(6)
    void delete_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when()
                .delete(URL + "/92182")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @Test
    @DisplayName("update() update an user")
    @Order(7)
    @Sql("/sql/user/init_one_user.sql")
    void update_UpdateUser_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-200.json");
        var users = repository.findAll();
        Assertions.assertThat(users).hasSize(1);

        request = request.replace("1", users.get(0).getId().toString());

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .put(URL)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("update() throw NotFoundException when no user is found")
    @Order(8)
    void update_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-404.json");
        var expectedResponse = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .put(URL)
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(expectedResponse));

    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSource")
    @DisplayName("save() returns bad request when fields are invalid")
    @Order(9)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFileName, String responseFileName) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(requestFileName));
        var expectedResponse = fileUtils.readResourceFile("user/%s".formatted(responseFileName));

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("timestamp")
                .asString()
                .isNotEmpty();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> postUserBadRequestSource() {

        return Stream.of(
                Arguments.of("post-request-user-blank-fields-400.json", "post-response-user-blank-fields-400.json"),
                Arguments.of("post-request-user-empty-fields-400.json", "post-response-user-empty-fields-400.json"),
                Arguments.of("post-request-user-invalid-email-400.json", "post-response-user-invalid-email-400.json")
        );
    }

    @ParameterizedTest
    @MethodSource("putUserBadRequestSource")
    @DisplayName("update() returns bad request when fields are invalid")
    @Order(10)
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFileName, String responseFileName) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(requestFileName));
        var expectedResponse = fileUtils.readResourceFile("user/%s".formatted(responseFileName));

        var response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("timestamp")
                .asString()
                .isNotEmpty();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> putUserBadRequestSource() {
        return Stream.of(
                Arguments.of("put-request-user-blank-fields-400.json", "put-response-user-blank-fields-400.json"),
                Arguments.of("put-request-user-empty-fields-400.json", "put-response-user-empty-fields-400.json"),
                Arguments.of("put-request-user-invalid-email-400.json", "put-response-user-invalid-email-400.json")
        );
    }

}

