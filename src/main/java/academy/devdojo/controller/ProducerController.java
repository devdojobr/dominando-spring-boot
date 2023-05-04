package academy.devdojo.controller;

import academy.devdojo.domain.Producer;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.response.ProducerPostResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping(path = {"v1/producers", "v1/producers/"})
public class ProducerController {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-version=v1")
    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request) {
        var producer = Producer.builder()
                .name(request.getName())
                .id(ThreadLocalRandom.current().nextLong(100_000))
                .createdAt(LocalDateTime.now())
                .build();
        Producer.getProducers().add(producer);
        var response = ProducerPostResponse.builder()
                .id(producer.getId())
                .name(producer.getName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
