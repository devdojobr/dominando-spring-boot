package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.config.IntegrationTestContainers;
import academy.devdojo.config.RestAssuredConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestAssuredConfig.class)
@Sql(value = "/sql/user/init_one_login_regular_user.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ProfileControllerRestAssuredIT extends IntegrationTestContainers {
    private static final String URL = "/v1/profiles";
    @Autowired
    private FileUtils fileUtils;
    @LocalServerPort
    private int port;

    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;

    @BeforeEach
    void setUrl() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Test
    @DisplayName("findAll() returns a list with all profiles")
    @Sql(value = "/sql/init_two_profiles.sql")
    void findAll_ReturnsAllProfiles_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("profile/get-all-profiles-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("findAll() returns an empty list when no profiles are found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNoProfilesAreFound() throws Exception {
        var response = fileUtils.readResourceFile("profile/get-all-profiles-empty-list-200.json");

        RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("save() creates profile")
    @Order(3)
    void save_CreatesProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("profile/post-request-profile-200.json");
        var expectedResponse = fileUtils.readResourceFile("profile/post-response-profile-201.json");

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

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSource")
    @DisplayName("save() returns bad request when fields are invalid")
    @Order(4)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String requestFileName, String responseFileName) throws Exception {
        var request = fileUtils.readResourceFile("profile/%s".formatted(requestFileName));
        var expectedResponse = fileUtils.readResourceFile("profile/%s".formatted(responseFileName));

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

    private static Stream<Arguments> postProfileBadRequestSource() {

        return Stream.of(
                Arguments.of("post-request-profile-blank-fields-400.json", "post-response-profile-blank-fields-400.json"),
                Arguments.of("post-request-profile-empty-fields-400.json", "post-response-profile-empty-fields-400.json")
        );
    }
}
