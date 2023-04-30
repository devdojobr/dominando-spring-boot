package academy.devdojo.controller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("greetings")
public class HelloController {

    @GetMapping("hi")
    public String hi() {
        return "OMAE WA MOU SHINDEIRU";
    }
}
