package academy.devdojo.commons;

import academy.devdojo.domain.Anime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnimeUtils {

    public List<Anime> newAnimeList() {
        var shingekiNoKyojin = Anime.builder().id(1L).name("ShingekiNoKyojin").build();
        var steinsGate = Anime.builder().id(2L).name("Steins Gate").build();
        var mashle = Anime.builder().id(3L).name("Mashle").build();
        return new ArrayList<>(List.of(shingekiNoKyojin, steinsGate, mashle));
    }

    public Anime newAnimeToSave() {
        return Anime.builder()
                .id(99L)
                .name("Tengoku Daimakyou")
                .build();
    }
}
