package academy.devdojo.domain;

import java.util.List;

public class Anime {
    private Long id;
    private String name;

    public Anime(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Anime> getAnimes() {
        var jigokuraku = new Anime(1L, "Jigokuraku");
        var konosuba = new Anime(2L, "Konosuba");
        var drStone = new Anime(3L, "Dr.Stone");
        return List.of(jigokuraku, konosuba, drStone);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
