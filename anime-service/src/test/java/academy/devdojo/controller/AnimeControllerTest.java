package academy.devdojo.controller;

import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.commons.FileUtils;
import academy.devdojo.domain.Anime;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.mapper.AnimeMapperImpl;
import academy.devdojo.service.AnimeService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(AnimeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({AnimeMapperImpl.class, FileUtils.class, AnimeUtils.class})
class AnimeControllerTest {
    private static final String URL = "/v1/animes";
    private static final String NAME = "name";
    private static final String ANIME_NOT_FOUND = "Anime not found";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AnimeService animeService;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private AnimeUtils animeUtils;


    @Test
    @DisplayName("findAll() returns a list with all animes")
    @Order(1)
    void findAll_ReturnsAllAnimes_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("anime/get-anime-null-name-200.json");

        BDDMockito.when(animeService.findAll(null)).thenReturn(animeUtils.newAnimeList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }
    @Test
    @DisplayName("findAll() returns a paginated list with all animes")
    @Order(1)
    void findAll_ReturnsAllAnimesPaginated_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("anime/get-anime-paginated-200.json");
        var animes = animeUtils.newAnimeList();
        var pageRequest = PageRequest.of(0, animes.size());
        var pagedAnimes = new PageImpl<>(animes, pageRequest, 1);

        BDDMockito.when(animeService.findAllPageable(BDDMockito.any(Pageable.class))).thenReturn(pagedAnimes);

        mockMvc.perform(MockMvcRequestBuilders.get(URL+"/paginated"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findAll() returns a list with found animes when name is not null")
    @Order(2)
    void findAll_ReturnsFoundAnimes_WhenNameIsPassedAndFound() throws Exception {
        var name = "Mashle";
        var response = fileUtils.readResourceFile("anime/get-anime-mashle-name-200.json");
        var animesFound = animeUtils.newAnimeList().stream().filter(anime -> anime.getName().equals(name)).toList();
        BDDMockito.when(animeService.findAll(name)).thenReturn(animesFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns an empty list when no anime is found by name")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNoNameIsFound() throws Exception {
        var name = "x";
        var response = fileUtils.readResourceFile("anime/get-anime-x-name-200.json");

        BDDMockito.when(animeService.findAll(name)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() returns a optional anime when id exists")
    @Order(4)
    void findById_ReturnsOptionalAnime_WhenIdExists() throws Exception {
        var response = fileUtils.readResourceFile("anime/get-anime-by-id-200.json");
        var id = 1L;
        var animeFound = animeUtils
                .newAnimeList()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);

        BDDMockito.when(animeService.findById(id)).thenReturn(animeFound);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("findById() throw NotFound when no anime is found")
    @Order(5)
    void findById_ThrowsNotFound_WhenNoAnimeIsFound() throws Exception {
        var response = fileUtils.readResourceFile("anime/anime-response-not-found-error-404.json");

        BDDMockito.when(animeService.findById(ArgumentMatchers.any()))
                .thenThrow(new NotFoundException(ANIME_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 100L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("save() creates anime")
    @Order(4)
    void save_CreatesAnime_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("anime/post-request-anime-200.json");
        var response = fileUtils.readResourceFile("anime/post-response-anime-201.json");
        var animeToSave = animeUtils.newAnimeToSave();

        BDDMockito.when(animeService.save(ArgumentMatchers.any())).thenReturn(animeToSave);

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
    @DisplayName("update() update an anime")
    @Order(5)
    void update_UpdateAnime_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("anime/put-request-anime-200.json");

        BDDMockito.doNothing().when(animeService).update(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() throw NotFoundException when no anime is found")
    @Order(6)
    void update_ThrowsNotFoundException_WhenNoAnimeIsFound() throws Exception {
        var request = fileUtils.readResourceFile("anime/put-request-anime-404.json");
        var response = fileUtils.readResourceFile("anime/anime-response-not-found-error-404.json");

        BDDMockito.doThrow(new NotFoundException(ANIME_NOT_FOUND))
                .when(animeService)
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

    @Test
    @DisplayName("delete() removes an anime")
    @Order(7)
    void delete_RemovesAnime_WhenSuccessful() throws Exception {
        BDDMockito.doNothing().when(animeService).delete(ArgumentMatchers.any());
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() throw NotFoundException when no anime is found")
    @Order(8)
    void delete_ThrowsNotFoundException_WhenNoAnimeIsFound() throws Exception {
        var id = 1111L;
        var response = fileUtils.readResourceFile("anime/anime-response-not-found-error-404.json");
        BDDMockito.doThrow(new NotFoundException(ANIME_NOT_FOUND))
                .when(animeService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @ParameterizedTest
    @MethodSource("postAnimeBadRequestSource")
    @DisplayName("save() returns bad request when fields are invalid")
    @Order(9)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("anime/%s".formatted(fileName));

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
    @MethodSource("putAnimeBadRequestSource")
    @DisplayName("update() returns bad request when fields are invalid")
    @Order(10)
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("anime/%s".formatted(fileName));

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

    private static Stream<Arguments> putAnimeBadRequestSource() {
        var errorsMap = allErrorsMap();
        var allErrors = new ArrayList<>(errorsMap.values());

        return Stream.of(
                Arguments.of("put-request-anime-blank-fields-400.json", allErrors),
                Arguments.of("put-request-anime-empty-fields-400.json", allErrors)
        );
    }

    private static Stream<Arguments> postAnimeBadRequestSource() {
        var errorsMap = allErrorsMap();
        var allErrors = new ArrayList<>(errorsMap.values());

        return Stream.of(
                Arguments.of("post-request-anime-blank-fields-400.json", allErrors),
                Arguments.of("post-request-anime-empty-fields-400.json", allErrors));
    }

    private static Map<String, String> allErrorsMap() {
        var nameError = "The field 'name' is required";
        return Map.of(NAME, nameError);
    }
}