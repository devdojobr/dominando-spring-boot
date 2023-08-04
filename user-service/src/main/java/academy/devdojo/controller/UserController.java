package academy.devdojo.controller;

import academy.devdojo.exception.ApiError;
import academy.devdojo.exception.DefaultErrorMessage;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/users"})
@Log4j2
@RequiredArgsConstructor
@Tag(name = "User API", description = "User related endpoints")
@SecurityRequirement(name = "basicAuth")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping
    @Operation(summary = "Get All Users", description = "Get all users available in the system",
    responses = {
            @ApiResponse(description = "List all users",
            responseCode = "200",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserGetResponse.class))))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetResponse>> list() {
        log.debug("Request received to list all users");

        var users = userService.findAll();

        var userGetResponses = mapper.toUserGetResponses(users);

        return ResponseEntity.ok(userGetResponses);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get user by id",
            responses = {
                    @ApiResponse(description = "Get a user by its id",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetResponse.class))),
                    @ApiResponse(description = "User Not Found",
                            responseCode = "404",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultErrorMessage.class)))
            })
    public ResponseEntity<UserGetResponse> findById(@PathVariable @Parameter(description = "user id") Long id) {
        log.info("Request received find user by id '{}'", id);
        var userFound = userService.findById(id);

        var response = mapper.toUserGetResponse(userFound);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create User",
            responses = {
                    @ApiResponse(description = "Save a user in the database",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPostResponse.class))),
                    @ApiResponse(description = "Email already exists",
                            responseCode = "400",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
            })
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
