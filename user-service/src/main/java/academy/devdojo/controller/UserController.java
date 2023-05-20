package academy.devdojo.controller;

import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserGetResponse;
import academy.devdojo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/users", "v1/users/"})
@Log4j2
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;
    @GetMapping
    public ResponseEntity<List<UserGetResponse>> list() {
        log.debug("Request received to list all users");

        var users = userService.findAll();

        var userGetResponses = mapper.toUserGetResponses(users);

        return ResponseEntity.ok(userGetResponses);
    }
}
