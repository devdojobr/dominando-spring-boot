package academy.devdojo.repository;


import academy.devdojo.domain.Anime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnimeHardCodedRepositoryTest {
    @InjectMocks
    private AnimeHardCodedRepository repository;
    @Mock
    private AnimeData animeData;
    private List<Anime> animes;

    @BeforeEach
    void init(){
        var shingekiNoKyojin = Anime.builder().id(1L).name("ShingekiNoKyojin").build();
        var steinsGate = Anime.builder().id(2L).name("Steins Gate").build();
        var mashle = Anime.builder().id(3L).name("Mashle").build();
        animes = new ArrayList<>(List.of(shingekiNoKyojin, steinsGate, mashle));

        BDDMockito.when(animeData.getAnimes()).thenReturn(animes);
    }
    @Test
    @DisplayName("findAll() returns a list with all animes")
    @Order(1)
    void findAll_ReturnsAllAnimes_WhenSuccessful(){
        var animes = repository.findAll();
        Assertions.assertThat(animes).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findById() returns an object with given id")
    @Order(2)
    void findById_ReturnsAAnime_WhenSuccessful(){
        var animeOptional = repository.findById(3L);
        Assertions.assertThat(animeOptional).isPresent().contains(animes.get(2));
    }

    @Test
    @DisplayName("findByName() returns all animes when name is null")
    @Order(3)
    void findByName_ReturnsAllAnimes_WhenNameIsNull(){
        var animes = repository.findByName(null);
        Assertions.assertThat(animes).hasSameElementsAs(this.animes);
    }

    @Test
    @DisplayName("findByName() returns list with filtered animes when name is not null")
    @Order(4)
    void findByName_ReturnsFilteredAnimes_WhenNameIsNotNull(){
        var animes = repository.findByName("ShingekiNoKyojin");
        Assertions.assertThat(animes).hasSize(1).contains(this.animes.get(0));
    }

    @Test
    @DisplayName("findByName() returns empty list when no anime is found")
    @Order(5)
    void findByName_ReturnsEmptyListOfAnimes_WhenNothingIsFound(){
        var animes = repository.findByName("XXXX");
        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save() creates a anime")
    @Order(6)
    void save_CreatesAnime_WhenSuccessful(){
        var animeToSave = Anime.builder()
                .id(99L)
                .name("Hajime No Ippo")
                .build();
        var anime = repository.save(animeToSave);

        Assertions.assertThat(anime)
                .isEqualTo(animeToSave)
                .hasNoNullFieldsOrProperties();

        var animes = repository.findAll();
        Assertions.assertThat(animes).contains(animeToSave);
    }

    @Test
    @DisplayName("delete() removes a anime")
    @Order(7)
    void delete_RemovesAnime_WhenSuccessful(){
        var animeToDelete = this.animes.get(0);
        repository.delete(animeToDelete);

        Assertions.assertThat(this.animes).doesNotContain(animeToDelete);
    }

    @Test
    @DisplayName("update() update a anime")
    @Order(8)
    void update_UpdateAnime_WhenSuccessful(){
        var animeToUpdate = this.animes.get(0);
        animeToUpdate.setName("Dragon Ball Z");

        repository.update(animeToUpdate);

        Assertions.assertThat(this.animes).contains(animeToUpdate);
        this.animes
                .stream()
                .filter(anime-> anime.getId().equals(animeToUpdate.getId()))
                .findFirst()
                .ifPresent(anime -> Assertions.assertThat(anime.getName()).isEqualTo(animeToUpdate.getName()));
    }
}