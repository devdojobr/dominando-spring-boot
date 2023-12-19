package academy.devdojo.service;

import academy.devdojo.commons.CepUtils;
import academy.devdojo.config.BrasilApiConfigurationProperties;
import academy.devdojo.config.RestClientConfiguration;
import academy.devdojo.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@RestClientTest({BrasilApiService.class,
        RestClientConfiguration.class,
        BrasilApiConfigurationProperties.class,
        CepUtils.class,
        ObjectMapper.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrasilApiServiceTest {
    @Autowired
    private BrasilApiService service;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private BrasilApiConfigurationProperties properties;
    @Autowired
    private CepUtils cepUtils;
    @Autowired
    private ObjectMapper mapper;

    @AfterEach
    void reset() {
        server.reset();
    }

    @Order(1)
    @Test
    @DisplayName("findCep returns CepGetResponse when successful")
    void findCep_ReturnsCepGetResponse_WhenSuccessful() throws JsonProcessingException {
        var cep = "00000";
        var cepGetResponse = cepUtils.newCepGetResponse();
        var jsonResponse = mapper.writeValueAsString(cepGetResponse);
        var requestTo = MockRestRequestMatchers.requestToUriTemplate(properties.baseUrl() + properties.uri(), cep);
        var withSuccess = MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON);
        server.expect(requestTo).andRespond(withSuccess);

        Assertions.assertThat(service.findCep(cep))
                .isNotNull()
                .isEqualTo(cepGetResponse);
    }

    @Order(2)
    @Test
    @DisplayName("findCep returns CepErrorResponse when unsuccessful")
    void findCep_ReturnsCepErrorResponse_WhenUnsuccessful() throws JsonProcessingException {
        var cep = "4040000";
        var cepErrorResponse = cepUtils.newCepErrorResponse();
        var jsonResponse = mapper.writeValueAsString(cepErrorResponse);
        var expectedErrorMessage = """
                404 NOT_FOUND "CepErrorResponse[name=CepPromiseError, message=Todos os serviços de CEP retornaram erro., type=service_error, errors=[CepInnerErrorResponse[name=ServiceError, message=CEP INVÁLIDO, service=correios]]]"
                """.trim();
        var requestTo = MockRestRequestMatchers.requestToUriTemplate(properties.baseUrl() + properties.uri(), cep);
        var withResourceNotFound = MockRestResponseCreators.withResourceNotFound().body(jsonResponse);
        server.expect(requestTo).andRespond(withResourceNotFound);

        Assertions.assertThatException()
                .isThrownBy(() -> service.findCep(cep))
                .withMessage(expectedErrorMessage)
                .isInstanceOf(NotFoundException.class);
    }
}