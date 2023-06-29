package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.commons.UserProfileUtils;
import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.repository.UserProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileServiceTest {
    @InjectMocks
    private UserProfileService service;
    @Mock
    private UserProfileRepository repository;
    private List<UserProfile> userProfiles;
    @InjectMocks
    private UserProfileUtils userProfileUtils;
    @Spy
    private UserUtils userUtils;
    @Spy
    private ProfileUtils profileUtils;

    @BeforeEach
    void init() {
        userProfiles = userProfileUtils.newUserProfileList();
    }

    @Test
    @DisplayName("findAll() returns a list with all user profiles")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(this.userProfiles);

        var userProfiles = service.findAll();
        assertThat(userProfiles).hasSameElementsAs(this.userProfiles);
    }

    @Test
    @DisplayName("findAllUsersByProfileId() returns a list of users for a given profile")
    @Order(2)
    void findAllUsersByProfileId_ReturnsAllUsersForGivenProfile_WhenSuccessful() {
        var profileId = 99L;

        var usersByProfile = this.userProfiles
                .stream()
                .filter(userProfile -> userProfile.getProfile().getId().equals(profileId))
                .map(UserProfile::getUser).toList();

        BDDMockito.when(repository.findAllUsersByProfileId(profileId))
                .thenReturn(usersByProfile);

        var users = service.findAllUsersByProfileId(profileId);

        assertThat(users).hasSize(1)
                .doesNotContainNull()
                .hasSameElementsAs(usersByProfile);

        users.forEach(user -> Assertions.assertThat(user).hasNoNullFieldsOrProperties());
    }
}