package academy.devdojo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("greetings")
@Log4j2
public class HelloController {

    @GetMapping("hi")
    public String hi() {
        return "OMAE WA MOU SHINDEIRU";
    }

    @PostMapping
    public Long save(@RequestBody String name) {
        log.info("Saving name '{}'", name);
        return ThreadLocalRandom.current().nextLong();
    }
}
