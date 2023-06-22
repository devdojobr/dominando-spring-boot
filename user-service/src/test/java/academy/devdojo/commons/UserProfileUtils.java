package academy.devdojo.commons;

import academy.devdojo.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProfileUtils {
    private final UserUtils userUtils;
    private final ProfileUtils profileUtils;

    public List<UserProfile> newUserProfileList() {
        var regularUser = newUserProfileSaved();
        return new ArrayList<>(List.of(regularUser));
    }

    public UserProfile newUserProfileToSave() {
        return UserProfile.builder()
                .user(userUtils.newUserSaved())
                .profile(profileUtils.newProfileSaved())
                .build();
    }

    public UserProfile newUserProfileSaved() {
        return UserProfile.builder()
                .id(1L)
                .user(userUtils.newUserSaved())
                .profile(profileUtils.newProfileSaved())
                .build();
    }
}
