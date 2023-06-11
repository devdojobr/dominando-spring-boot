package academy.devdojo.service;

import academy.devdojo.commons.AnimeUtils;
import academy.devdojo.domain.Anime;
import academy.devdojo.repository.AnimeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeServiceTest {
    @InjectMocks
    private AnimeService service;
    @Mock
    private AnimeRepository repository;
    @InjectMocks
    private AnimeUtils animeUtils;
    private List<Anime> animes;

    @BeforeEach
    void init() {
        animes = animeUtils.newAnimeList();
    }

    @Test
    @DisplayName("findAll() returns a list with all animes")
    @Order(1)
    void findAll_ReturnsAllAnimes_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(this.animes);

        var animes = service.findAll(null);
        Assertions.assertThat(animes).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findAll() returns a paginated list with all animes")
    @Order(1)
    void findAll_ReturnsAllAnimesPaginated_WhenSuccessful() {
        var pageRequest = PageRequest.of(0, this.animes.size());
        var pagedAnimes = new PageImpl<>(this.animes, pageRequest, 1);

        BDDMockito.when(repository.findAll(BDDMockito.any(Pageable.class)))
                .thenReturn(pagedAnimes);

        var animesFound = service.findAllPageable(pageRequest);
        Assertions.assertThat(animesFound).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findAll() returns a list with found animes when name is not null")
    @Order(2)
    void findAll_ReturnsFoundAnimes_WhenNameIsPassedAndFound() {
        var name = "Mashle";
        var animesFound = this.animes.stream().filter(anime -> anime.getName().equals(name)).toList();
        BDDMockito.when(repository.findByName(name)).thenReturn(animesFound);

        var animes = service.findAll(name);
        Assertions.assertThat(animes).hasSize(1).contains(animesFound.get(0));
    }

    @Test
    @DisplayName("findAll() returns an empty list when no anime is found by name")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNoNameIsFound() {
        var name = "x";
        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());

        var animes = service.findAll(name);

        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById() returns a optional anime when id exists")
    @Order(4)
    void findById_ReturnsOptionalAnime_WhenIdExists() {
        var id = 1L;
        var animeExpected = this.animes.get(0);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(animeExpected));

        var animeOptional = service.findById(id);

        Assertions.assertThat(animeOptional).isEqualTo(animeExpected);
    }

    @Test
    @DisplayName("findById() throw ResponseStatusException when no anime is found")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenNoAnimeIsFound() {
        var id = 1L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Assertions
                .assertThatException()
                .isThrownBy(() -> service.findById(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("save() creates anime")
    @Order(6)
    void save_CreatesAnime_WhenSuccessful() {
        var animeToSave = animeUtils.newAnimeToSave();

        BDDMockito.when(repository.save(animeToSave)).thenReturn(animeToSave);
        var anime = service.save(animeToSave);

        Assertions.assertThat(anime)
                .isEqualTo(animeToSave)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete() removes an anime")
    @Order(7)
    void delete_RemovesAnime_WhenSuccessful() {
        var id = 1L;
        var animeToDelete = this.animes.get(0);
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(animeToDelete));
        BDDMockito.doNothing().when(repository).delete(animeToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(id));
    }

    @Test
    @DisplayName("delete() throw ResponseStatusException when no anime is found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenNoAnimeIsFound() {
        var id = 1L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("update() update an anime")
    @Order(9)
    void update_UpdateAnime_WhenSuccessful() {
        var id = 1L;
        var animeToUpdate = this.animes.get(0);
        animeToUpdate.setName("DBZ");

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(animeToUpdate));
        BDDMockito.when(repository.save(animeToUpdate)).thenReturn(animeToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(animeToUpdate));
    }

    @Test
    @DisplayName("update() throw ResponseStatusException when no anime is found")
    @Order(10)
    void update_ThrowsResponseStatusException_WhenNoAnimeIsFound() {
        var id = 1L;
        var animeToUpdate = this.animes.get(0);
        animeToUpdate.setName("Naruto");

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(animeToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }

}