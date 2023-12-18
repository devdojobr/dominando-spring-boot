package academy.devdojo.service;

import academy.devdojo.config.BrasilApiConfigurationProperties;
import academy.devdojo.response.CepGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BrasilApiService {
    private final RestClient brasilApiRestClient;
    private final BrasilApiConfigurationProperties brasilApiConfigurationProperties;

    public CepGetResponse findCep(String cep) {
        return brasilApiRestClient
                .get()
                .uri(brasilApiConfigurationProperties.uri(), cep)
                .retrieve()
                .body(CepGetResponse.class);
    }
}
