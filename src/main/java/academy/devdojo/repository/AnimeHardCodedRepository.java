package academy.devdojo.repository;

import academy.devdojo.domain.Anime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimeHardCodedRepository {
    private static final List<Anime> ANIMES = new ArrayList<>();

    static {
        var jigokuraku =  Anime.builder().id(1L).name("Jigokuraku").build();
        var konosuba =  Anime.builder().id(2L).name("Konosuba").build();
        var drStone =  Anime.builder().id(3L).name("Dr.Stone").build();
        ANIMES.addAll(List.of(jigokuraku, konosuba, drStone));
    }

    public List<Anime> findAll() {
        return ANIMES;
    }

    public Optional<Anime> findById(Long id) {
        return ANIMES.stream().filter(anime -> anime.getId().equals(id)).findFirst();
    }

    public List<Anime> findByName(String name) {
        return name == null ? ANIMES :
                ANIMES.stream()
                        .filter(anime -> anime.getName().equalsIgnoreCase(name))
                        .toList();
    }

    public Anime save(Anime anime) {
        ANIMES.add(anime);
        return anime;
    }

    public void delete(Anime anime) {
        ANIMES.remove(anime);
    }

    public void update(Anime anime) {
        delete(anime);
        save(anime);
    }
}
