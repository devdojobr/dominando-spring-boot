package academy.devdojo.controller;

import academy.devdojo.mapper.ProfileMapper;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.ProfilePostRequest;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/profiles", "v1/profiles/"})
@Log4j2
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileMapper mapper;

    @GetMapping
    public ResponseEntity<List<ProfileGetResponse>> list() {
        log.debug("Request received to list all profiles");

        var profiles = profileService.findAll();

        var profilesGetResponses = mapper.toProfileGetResponses(profiles);

        return ResponseEntity.ok(profilesGetResponses);
    }

    @PostMapping
    public ResponseEntity<ProfilePostResponse> save(@RequestBody @Valid ProfilePostRequest request) {
        log.info("Request received save a profile '{}'", request);

        var profile = mapper.toProfile(request);

        profile = profileService.save(profile);

        var response = mapper.toProfilePostResponse(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}