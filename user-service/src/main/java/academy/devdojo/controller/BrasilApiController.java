package academy.devdojo.controller;

import academy.devdojo.config.BrasilApiConfigurationProperties;
import academy.devdojo.response.CepGetResponse;
import academy.devdojo.service.BrasilApiService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"v1/brasil-api/cep", "v1/brasil-api/cep/"})
@Log4j2
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class BrasilApiController {
    private final BrasilApiConfigurationProperties brasilApiConfigurationProperties;
    private final BrasilApiService brasilApiService;

    @GetMapping("/{cep}")
    public ResponseEntity<CepGetResponse> list(@PathVariable String cep) {
        log.debug("Request received to find the cep {}", cep);
        var cepGetResponse = brasilApiService.findCep(cep);

        return ResponseEntity.ok(cepGetResponse);
    }

}