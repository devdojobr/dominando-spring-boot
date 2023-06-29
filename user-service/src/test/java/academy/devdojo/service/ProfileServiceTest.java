package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import academy.devdojo.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileServiceTest {
    @InjectMocks
    private ProfileService service;
    @Mock
    private ProfileRepository repository;
    private List<Profile> profiles;
    @InjectMocks
    private ProfileUtils profileUtils;

    @BeforeEach
    void init() {
        profiles = profileUtils.newProfileList();
    }

    @Test
    @DisplayName("findAll() returns a list with all profiles")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(this.profiles);

        var profiles = service.findAll();
        assertThat(profiles).hasSameElementsAs(this.profiles);
    }

    @Test
    @DisplayName("save() creates profile")
    @Order(2)
    void save_CreatesProfile_WhenSuccessful() {
        var profileToSave = profileUtils.newProfileToSave();
        var profileSaved = profileUtils.newProfileSaved();

        BDDMockito.when(repository.save(profileToSave)).thenReturn(profileSaved);
        var profile = service.save(profileToSave);

        Assertions.assertThat(profile)
                .isEqualTo(profileSaved)
                .hasNoNullFieldsOrProperties();
    }
}