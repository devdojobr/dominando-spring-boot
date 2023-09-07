package academy.devdojo.controller;

import academy.devdojo.api.AnimeControllerApi;
import academy.devdojo.dto.*;
import academy.devdojo.mapper.AnimeMapper;
import academy.devdojo.service.AnimeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/animes"})
@Log4j2
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class AnimeController implements AnimeControllerApi {
    private final AnimeMapper mapper;
    private final AnimeService animeService;

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> listAllAnime(@RequestParam(required = false) String name) {
        log.info("Request received to list all animes, param name '{}'", name);

        var animes = animeService.findAll(name);

        var animeGetResponses = mapper.toAnimeGetResponses(animes);

        return ResponseEntity.ok(animeGetResponses);
    }

    @Override
    @GetMapping("/paginated")
    public ResponseEntity<PageAnimeGetResponse> listAllAnimePaginated(
            @Min(0) @Parameter(name = "page", description = "Zero-based page index (0..N)", in = ParameterIn.QUERY) @Valid @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Min(1) @Parameter(name = "size", description = "The size of the page to be returned", in = ParameterIn.QUERY) @Valid @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @Parameter(name = "sort", description = "Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "sort", required = false) List<String> sort,
            @ParameterObject final Pageable pageable
    ) {
        log.info("Request received to list all animes paginated");

        var jpaPageAnimeGetResponse = animeService.findAllPageable(pageable);
        var pageAnimeGetResponse = mapper.toPageAnimeGetResponse(jpaPageAnimeGetResponse);
        return ResponseEntity.ok(pageAnimeGetResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<AnimeGetResponse> findAnimeById(@PathVariable Long id) {
        log.info("Request received find anime by id '{}'", id);
        var animeFound = animeService.findById(id);

        var response = mapper.toAnimeGetResponse(animeFound);

        return ResponseEntity.ok(response);
    }

    // Idempotência
    @PostMapping
    public ResponseEntity<AnimePostResponse> saveAnime(@RequestBody @Valid AnimePostRequest request) {
        log.info("Request received save anime '{}'", request);

        var anime = mapper.toAnime(request);

        anime = animeService.save(anime);

        var response = mapper.toAnimePostResponse(anime);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAnimeById(@PathVariable Long id) {
        log.info("Request received to delete the anime by id '{}'", id);
        animeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateAnime(@RequestBody @Valid AnimePutRequest request) {
        log.info("Request received to update the anime '{}'", request);

        var animeToUpdate = mapper.toAnime(request);

        animeService.update(animeToUpdate);

        return ResponseEntity.noContent().build();
    }
}
