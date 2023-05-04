package academy.devdojo.controller;

import academy.devdojo.domain.Producer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping(path = {"v1/producers", "v1/producers/"})
public class ProducerController {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
    headers = "x-api-version=v1")
    public ResponseEntity<Producer> save(@RequestBody Producer producer) {
        producer.setId(ThreadLocalRandom.current().nextLong(100_000));
        Producer.getProducers().add(producer);
        return ResponseEntity.status(HttpStatus.CREATED).body(producer);
//        return ResponseEntity.ok(producer);
//        return ResponseEntity.noContent().build();
    }
}
