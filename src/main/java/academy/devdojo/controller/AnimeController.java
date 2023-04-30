package academy.devdojo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/animes", "v1/animes/"})
public class AnimeController {
    @GetMapping
    public List<String> list() {
        return List.of("Hell's Paradise (Jigokuraku)", "Dr.Stone", "Konosuba");
    }
}
