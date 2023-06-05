package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.mapper.UserMapperImpl;
import academy.devdojo.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@WebMvcTest(UserController.class)
@Import({UserMapperImpl.class, FileUtils.class, UserUtils.class})
class UserControllerTest {
    private static final String URL = "/v1/users";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String USER_NOT_FOUND = "User not found";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("findAll() returns a list with all users")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("user/get-all-users-200.json");
        BDDMockito.when(userService.findAll()).thenReturn(userUtils.newUserList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns an empty list when no users are found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNoUsersAreFound() throws Exception {
        var response = fileUtils.readResourceFile("user/get-all-users-empty-list-200.json");
        BDDMockito.when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() returns a optional user when id exists")
    @Order(3)
    void findById_ReturnsOptionalAnime_WhenIdExists() throws Exception {
        var id = 1L;
        var response = fileUtils.readResourceFile("user/get-user-by-id-200.json");
        var userFound = userUtils
                .newUserList()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
        BDDMockito.when(userService.findById(id)).thenReturn(userFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() throw NotFoundException when no user is found")
    @Order(4)
    void findById_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");
        BDDMockito.when(userService.findById(ArgumentMatchers.any()))
                .thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 100L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("save() creates user")
    @Order(4)
    void save_CreatesUser_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("user/post-request-user-200.json");
        var response = fileUtils.readResourceFile("user/post-response-user-201.json");
        var userToSave = userUtils.newUserToSave();

        BDDMockito.when(userService.save(ArgumentMatchers.any())).thenReturn(userToSave);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("delete() removes a user")
    @Order(5)
    void delete_RemovesUser_WhenSuccessful() throws Exception {
        BDDMockito.doNothing().when(userService)
                .delete(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() throw NotFoundException when no user is found")
    @Order(6)
    void delete_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var id = 1111L;
        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");
        BDDMockito.doThrow(new NotFoundException(USER_NOT_FOUND))
                .when(userService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("update() update an user")
    @Order(7)
    void update_UpdateUser_WhenSuccessful() throws Exception {
        BDDMockito.doNothing().when(userService).update(ArgumentMatchers.any());

        var request = fileUtils.readResourceFile("user/put-request-user-200.json");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() throw NotFoundException when no user is found")
    @Order(8)
    void update_ThrowsNotFoundException_WhenNoUserIsFound() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-404.json");
        var response = fileUtils.readResourceFile("user/user-response-not-found-error-404.json");

        BDDMockito.doThrow(new NotFoundException(USER_NOT_FOUND))
                .when(userService)
                .update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @ParameterizedTest
    @MethodSource("postUserBadRequestSource")
    @DisplayName("save() returns bad request when fields are invalid")
    @Order(9)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    @ParameterizedTest
    @MethodSource("putUserBadRequestSource")
    @DisplayName("update() returns bad request when fields are invalid")
    @Order(10)
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("user/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();

        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> putUserBadRequestSource() {
        var errorsMap = allErrorsMap();
        var emailError = Collections.singletonList(errorsMap.get(EMAIL));
        var allErrors = new ArrayList<>(errorsMap.values());
        return Stream.of(
                Arguments.of("put-request-user-blank-fields-400.json", allErrors),
                Arguments.of("put-request-user-empty-fields-400.json", allErrors),
                Arguments.of("put-request-user-invalid-email-400.json", emailError)
        );
    }

    private static Stream<Arguments> postUserBadRequestSource() {
        var errorsMap = allErrorsMap();
        var emailError = Collections.singletonList(errorsMap.get(EMAIL));
        var allErrors = new ArrayList<>(errorsMap.values());

        return Stream.of(
                Arguments.of("post-request-user-blank-fields-400.json", allErrors),
                Arguments.of("post-request-user-empty-fields-400.json", allErrors),
                Arguments.of("post-request-user-invalid-email-400.json", emailError)
        );
    }

    private static Map<String, String> allErrorsMap() {
        var firstNameError = "The field 'firstName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailError = "The email format is not valid";
        return Map.of(
                FIRST_NAME, firstNameError,
                LAST_NAME, lastNameError,
                EMAIL, emailError);
    }
}