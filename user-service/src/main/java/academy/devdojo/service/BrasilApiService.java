package academy.devdojo.service;

import academy.devdojo.config.BrasilApiConfigurationProperties;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.response.CepErrorResponse;
import academy.devdojo.response.CepGetResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BrasilApiService {
    private final RestClient brasilApiRestClient;
    private final BrasilApiConfigurationProperties brasilApiConfigurationProperties;
    private final ObjectMapper mapper;

    public CepGetResponse findCep(String cep) {
        return brasilApiRestClient
                .get()
                .uri(brasilApiConfigurationProperties.uri(), cep)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    var cepErrorResponse = mapper.readValue(response.getBody().readAllBytes(), CepErrorResponse.class);
                    throw new NotFoundException(cepErrorResponse.toString());
                })
                .body(CepGetResponse.class);
    }
}
