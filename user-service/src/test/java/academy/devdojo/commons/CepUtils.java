package academy.devdojo.commons;

import academy.devdojo.response.CepGetResponse;
import org.springframework.stereotype.Component;

@Component
public class CepUtils {

    public CepGetResponse newCepGetResponse() {
        return CepGetResponse.builder()
                .cep("00000")
                .city("São Paulo")
                .neighborhood("Vila Mariana")
                .street("Rua 123")
                .service("viacep")
                .build();
    }
}
