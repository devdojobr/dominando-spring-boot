package academy.devdojo.controller;

import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.mapper.ProfileMapper;
import academy.devdojo.mapper.UserProfileMapper;
import academy.devdojo.response.UserProfileGetResponse;
import academy.devdojo.response.UserProfileUserGetResponse;
import academy.devdojo.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"v1/user-profiles", "v1/user-profiles/"})
@Log4j2
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final UserProfileMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserProfileGetResponse>> list() {
        log.debug("Request received to list all user userProfiles");

        var userProfiles = userProfileService.findAll();
        var userProfileGetResponses = mapper.toUserProfileGetResponse(userProfiles);
        return ResponseEntity.ok(userProfileGetResponses);
    }

    @GetMapping("profiles/{id}/users")
    public ResponseEntity<List<UserProfileUserGetResponse>> listAllUsersByProfileId(@PathVariable Long id) {
        log.debug("Request received to list all user users by profile id '{}'", id);

        var users = userProfileService.findAllUsersByProfileId(id);
        var userProfileUserGetResponses = mapper.toUserProfileUserGetResponse(users);
        return ResponseEntity.ok(userProfileUserGetResponses);
    }

}
