package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.mapper.UserMapperImpl;
import academy.devdojo.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@WebMvcTest(UserController.class)
@Import({UserMapperImpl.class, FileUtils.class, UserUtils.class})
class UserControllerTest {
    private static final String URL = "/v1/users";
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
    @DisplayName("findById() throw ResponseStatusException when no user is found")
    @Order(4)
    void findById_ThrowsResponseStatusException_WhenNoUserIsFound() throws Exception {
        BDDMockito.when(userService.findById(ArgumentMatchers.any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 100L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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
    @DisplayName("delete() throw ResponseStatusException when no user is found")
    @Order(6)
    void delete_ThrowsResponseStatusException_WhenNoUserIsFound() throws Exception {
        var id = 1111L;
        BDDMockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1111L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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
    @DisplayName("update() throw ResponseStatusException when no user is found")
    @Order(8)
    void update_ThrowsResponseStatusException_WhenNoUserIsFound() throws Exception {
        var request = fileUtils.readResourceFile("user/put-request-user-404.json");
        BDDMockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService)
                .update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("save() returns bad request when fields are empty")
    @Order(9)
    void save_ReturnsBadRequest_WhenFieldsAreEmpty() throws Exception {
        var request = fileUtils.readResourceFile("user/post-request-user-empty-fields-400.json");

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

        var firstNameError = "The field 'firstName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailNameError = "The email format is not valid";
        Assertions.assertThat(resolvedException.getMessage())
                .contains(firstNameError,lastNameError,emailNameError);

    }

    @Test
    @DisplayName("save() returns bad request when fields are blank")
    @Order(10)
    void save_ReturnsBadRequest_WhenFieldsAreBlank() throws Exception {
        var request = fileUtils.readResourceFile("user/post-request-user-blank-fields-400.json");

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

        var firstNameError = "The field 'firstName' is required";
        var lastNameError = "The field 'lastName' is required";
        var emailNameError = "The email format is not valid";
        Assertions.assertThat(resolvedException.getMessage())
                .contains(firstNameError,lastNameError,emailNameError);

    }
}