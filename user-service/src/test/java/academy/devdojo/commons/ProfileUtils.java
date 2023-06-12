package academy.devdojo.commons;

import academy.devdojo.domain.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileUtils {
    public List<Profile> newProfileList() {
        var admin = Profile.builder().id(1L).name("Administrator").description("Able to admin everything").build();
        var manager = Profile.builder().id(2L).name("Manager").description("Can manage some stuff").build();
        return new ArrayList<>(List.of(admin, manager));
    }

    public Profile newProfileToSave() {
        return Profile.builder()
                .name("Regular user")
                .description("Profile for regular user with regular permissions")
                .build();
    }

    public Profile newProfileSaved() {
        return Profile.builder()
                .id(99L)
                .name("Regular user")
                .description("Profile for regular user with regular permissions")
                .build();
    }
}
