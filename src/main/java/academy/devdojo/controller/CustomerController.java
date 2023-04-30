package academy.devdojo.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = {"v1/customers", "v1/customers/"})
public class CustomerController {
    public static final List<String> NAMES = List.of("William", "Brenon", "Wildnei", "Fabricio");

    @GetMapping
    public List<String> list() {
        return NAMES;
    }

    @GetMapping("filter")
    public List<String> filter(@RequestParam(defaultValue = "") String name) {
        return NAMES.stream().filter(n -> n.equalsIgnoreCase(name)).toList();
    }

    @GetMapping("filterOptional")
    public List<String> filter(@RequestParam Optional<String> name) {
        return NAMES.stream().filter(n -> n.equalsIgnoreCase(name.orElse(""))).toList();
    }

    @GetMapping("filterList")
    public List<String> filter(@RequestParam(name = "name") List<String> names) {
        return NAMES.stream().filter(names::contains).toList();
    }

    //http://localhost:8080/v1/customers?name=William
    //http://localhost:8080/v1/customers/{customerId}/payments/{paymentId}
    @GetMapping("{name}")
    public String findByName(@PathVariable String name){
        return NAMES.stream().filter(n -> n.equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> "");
    }
}
