package academy.devdojo.controller;

import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{id}")
    public ResponseEntity<UserGetResponse> findById(@PathVariable Long id) {
        log.info("Request received find user by id '{}'", id);
        var userFound = userService.findById(id);

        var response = mapper.toUserGetResponse(userFound);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest request) {
        log.info("Request received save a user '{}'", request);

        var user = mapper.toUser(request);

        user = userService.save(user);

        var response = mapper.toUserPostResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("Request received to delete the user by id '{}'", id);
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UserPutRequest request) {
        log.info("Request received to update the user '{}'", request);

        var userToUpdate = mapper.toUser(request);

        userService.update(userToUpdate);

        return ResponseEntity.noContent().build();
    }


}
