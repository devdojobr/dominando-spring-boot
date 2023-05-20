package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@WebMvcTest(UserController.class)
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
    void findById_ThrowsResponseStatusException_WhenNoAnimeIsFound() throws Exception {
        BDDMockito.when(userService.findById(ArgumentMatchers.any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 100L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}