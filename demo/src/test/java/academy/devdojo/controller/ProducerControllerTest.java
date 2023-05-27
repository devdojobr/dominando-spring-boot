package academy.devdojo.controller;

import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.commons.FileUtils;
import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.config.BeanConfig;
import academy.devdojo.mapper.AnimeMapperImpl;
import academy.devdojo.mapper.ProducerMapperImpl;
import academy.devdojo.repository.ProducerData;
import academy.devdojo.repository.ProducerHardCodedRepository;
import academy.devdojo.service.AnimeService;
import academy.devdojo.service.ProducerService;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import test.outside.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@WebMvcTest(ProducerController.class)
//@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({ProducerMapperImpl.class, FileUtils.class, ProducerUtils.class, ProducerService.class, BeanConfig.class})
class ProducerControllerTest {
    private static final String URL = "/v1/producers";
    private static final String NAME = "name";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProducerData producerData;
    @SpyBean
    private ProducerHardCodedRepository repository;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private ProducerUtils producerUtils;

    @BeforeEach
    void init() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerUtils.newProducerList());
    }

    @Test
    @DisplayName("findAll() returns a list with all producers")
    @Order(1)
    void findAll_ReturnsAllProducers_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("producer/get-producer-null-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns a list with found producers when name is not null")
    @Order(2)
    void findAll_ReturnsFoundProducers_WhenNameIsPassedAndFound() throws Exception {
        var name = "Ufotable";
        var response = fileUtils.readResourceFile("producer/get-producer-ufotable-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }

    @Test
    @DisplayName("findAll() returns an empty list when no producer is found by name")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNoNameIsFound() throws Exception {
        var name = "x";
        var response = fileUtils.readResourceFile("producer/get-producer-x-name-200.json");

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("save() creates producer")
    @Order(4)
    void save_CreatesProducer_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("producer/post-request-producer-200.json");
        var response = fileUtils.readResourceFile("producer/post-response-producer-201.json");
        var producerToSave = producerUtils.newProducerToSave();

        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(producerToSave);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .header("x-api-version", "v1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("update() update a producer")
    @Order(5)
    void update_UpdateProducer_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("producer/put-request-producer-200.json");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("update() throw ResponseStatusException when no producer is found")
    @Order(6)
    void update_ThrowsResponseStatusException_WhenNoProducerIsFound() throws Exception {
        var request = fileUtils.readResourceFile("producer/put-request-producer-404.json");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found to be updated"));
    }

    @Test
    @DisplayName("delete() removes a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("delete() throw ResponseStatusException when no producer is found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenNoProducerIsFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1111L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found to be deleted"));
    }

    @ParameterizedTest
    @MethodSource("postProducerBadRequestSource")
    @DisplayName("save() returns bad request when fields are invalid")
    @Order(9)
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("producer/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .header("x-api-version", "v1")
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
    @MethodSource("putProducerBadRequestSource")
    @DisplayName("update() returns bad request when fields are invalid")
    @Order(10)
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("producer/%s".formatted(fileName));

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

    private static Stream<Arguments> putProducerBadRequestSource() {
        var errorsMap = allErrorsMap();
        var allErrors = new ArrayList<>(errorsMap.values());

        return Stream.of(
                Arguments.of("put-request-producer-blank-fields-400.json", allErrors),
                Arguments.of("put-request-producer-empty-fields-400.json", allErrors)
        );
    }

    private static Stream<Arguments> postProducerBadRequestSource() {
        var errorsMap = allErrorsMap();
        var allErrors = new ArrayList<>(errorsMap.values());

        return Stream.of(
                Arguments.of("post-request-producer-blank-fields-400.json", allErrors),
                Arguments.of("post-request-producer-empty-fields-400.json", allErrors));
    }

    private static Map<String, String> allErrorsMap() {
        var nameError = "The field 'name' is required";
        return Map.of(NAME, nameError);
    }

}