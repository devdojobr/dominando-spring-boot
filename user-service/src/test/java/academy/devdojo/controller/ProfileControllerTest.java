package academy.devdojo.controller;

import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.mapper.ProfileMapperImpl;
import academy.devdojo.service.ProfileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
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

@WebMvcTest(ProfileController.class)
@Import({ProfileMapperImpl.class, FileUtils.class, ProfileUtils.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileControllerTest {
    private static final String URL = "/v1/profiles";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProfileService profileService;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private ProfileUtils profileUtils;

    @Test
    @DisplayName("findAll() returns a list with all profiles")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("profile/get-all-profiles-200.json");
        BDDMockito.when(profileService.findAll()).thenReturn(profileUtils.newProfileList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns an empty list when no profiles are found")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNoProfilesAreFound() throws Exception {
        var response = fileUtils.readResourceFile("profile/get-all-profiles-empty-list-200.json");
        BDDMockito.when(profileService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("save() creates profile")
    @Order(3)
    void save_CreatesProfile_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("profile/post-request-profile-200.json");
        var response = fileUtils.readResourceFile("profile/post-response-profile-201.json");
        var profileSaved = profileUtils.newProfileSaved();

        BDDMockito.when(profileService.save(ArgumentMatchers.any())).thenReturn(profileSaved);

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

    @ParameterizedTest
    @MethodSource("postProfileBadRequestSource")
    @DisplayName("save() returns bad request when fields are invalid")
    @Order(4)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("profile/%s".formatted(fileName));

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

    private static Stream<Arguments> postProfileBadRequestSource() {
        var errorsMap = allErrorsMap();
        var allErrors = new ArrayList<>(errorsMap.values());

        return Stream.of(
                Arguments.of("post-request-profile-blank-fields-400.json", allErrors),
                Arguments.of("post-request-profile-empty-fields-400.json", allErrors)
        );
    }

    private static Map<String, String> allErrorsMap() {
        var nameError = "The field 'name' is required";
        var descriptionError = "The field 'description' is required";
        return Map.of(
                NAME, nameError,
                DESCRIPTION, descriptionError);
    }
}