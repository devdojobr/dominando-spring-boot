package academy.devdojo.controller;

import academy.devdojo.api.ProducerControllerApi;
import academy.devdojo.dto.ProducerGetResponse;
import academy.devdojo.dto.ProducerPostRequest;
import academy.devdojo.dto.ProducerPostResponse;
import academy.devdojo.dto.ProducerPutRequest;
import academy.devdojo.mapper.ProducerMapper;
import academy.devdojo.service.ProducerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/producers"})
@Log4j2
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class ProducerController implements ProducerControllerApi {
    private final ProducerMapper mapper;
    private final ProducerService producerService;

    @GetMapping
    public ResponseEntity<List<ProducerGetResponse>> findAllProducer(@RequestParam(required = false) String name) {
        log.debug("Request received to list all producers, param name '{}'", name);
        var producers = producerService.findAll(name);

        var producerGetResponses = mapper.toProducerGetResponses(producers);

        return ResponseEntity.ok(producerGetResponses);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProducerPostResponse> saveProducer(@RequestBody @Valid ProducerPostRequest request) {
        var producer = mapper.toProducer(request);

        producer = producerService.save(producer);

        var response = mapper.toProducerPostResponse(producer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProducerById(@PathVariable Long id) {
        log.info("Request received to delete the producer by id '{}'", id);

        producerService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateProducer(@RequestBody @Valid ProducerPutRequest request) {
        log.info("Request received to update the producer '{}'", request);

        var producerToUpdate = mapper.toProducer(request);

        producerService.update(producerToUpdate);

        return ResponseEntity.noContent().build();
    }
}
