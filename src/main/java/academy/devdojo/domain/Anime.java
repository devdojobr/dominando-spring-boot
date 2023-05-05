package academy.devdojo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Anime {
    @EqualsAndHashCode.Include
    private Long id;
    @JsonProperty(value = "name")
    private String name;
    private static List<Anime> animes = new ArrayList<>();

    static {
        var jigokuraku =  Anime.builder().id(1L).name("Jigokuraku").build();
        var konosuba =  Anime.builder().id(2L).name("Konosuba").build();
        var drStone =  Anime.builder().id(3L).name("Dr.Stone").build();
        animes.addAll(List.of(jigokuraku, konosuba, drStone));
    }

    public static List<Anime> getAnimes() {
        return animes;
    }
}
