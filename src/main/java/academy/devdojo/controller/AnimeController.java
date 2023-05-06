package academy.devdojo.controller;

import academy.devdojo.mapper.AnimeMapper;
import academy.devdojo.request.AnimePostRequest;
import academy.devdojo.request.AnimePutRequest;
import academy.devdojo.response.AnimeGetResponse;
import academy.devdojo.response.AnimePostResponse;
import academy.devdojo.service.AnimeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/animes", "v1/animes/"})
@Log4j2
public class AnimeController {
    private static final AnimeMapper MAPPER = AnimeMapper.INSTANCE;
    private AnimeService animeService;

    public AnimeController() {
        this.animeService = new AnimeService();
    }

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> list(@RequestParam(required = false) String name) {
        log.info("Request received to list all animes, param name '{}'", name);

        var animes = animeService.findAll(name);

        var animeGetResponses = MAPPER.toAnimeGetResponses(animes);

        return ResponseEntity.ok(animeGetResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        log.info("Request received find anime by id '{}'", id);
        var animeFound = animeService.findById(id);

        var response = MAPPER.toAnimeGetResponse(animeFound);

        return ResponseEntity.ok(response);
    }

    // Idempotência
    @PostMapping
    public ResponseEntity<AnimePostResponse> save(@RequestBody AnimePostRequest request) {
        log.info("Request received save anime '{}'", request);
       
        var anime = MAPPER.toAnime(request);

        anime = animeService.save(anime);

        var response = MAPPER.toAnimePostResponse(anime);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("Request received to delete the anime by id '{}'", id);
        animeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody AnimePutRequest request) {
        log.info("Request received to update the anime '{}'", request);

        var animeToUpdate = MAPPER.toAnime(request);

        animeService.update(animeToUpdate);

        return ResponseEntity.noContent().build();
    }
}
