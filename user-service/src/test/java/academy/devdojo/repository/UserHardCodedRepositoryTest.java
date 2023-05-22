package academy.devdojo.repository;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserHardCodedRepositoryTest {
    @InjectMocks
    private UserHardCodedRepository repository;
    @Mock
    private UserData userData;
    private List<User> users;
    @InjectMocks
    private UserUtils userUtils;

    @BeforeEach
    void init(){
        users = userUtils.newUserList();

        BDDMockito.when(userData.getUsers()).thenReturn(users);
    }

    @Test
    @DisplayName("findAll() returns a list with all users")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenSuccessful(){
        var users = repository.findAll();
        Assertions.assertThat(users).hasSameElementsAs(this.users);
    }

    @Test
    @DisplayName("findById() returns an object with given id")
    @Order(2)
    void findById_ReturnsUser_WhenSuccessful() {
        var userOptional = repository.findById(3L);
        Assertions.assertThat(userOptional).isPresent().contains(users.get(2));
    }

    @Test
    @DisplayName("save() creates a user")
    @Order(3)
    void save_CreatesUser_WhenSuccessful() {
        var userToSave = userUtils.newUserToSave();
        var user = repository.save(userToSave);

        Assertions.assertThat(user)
                .isEqualTo(userToSave)
                .hasNoNullFieldsOrProperties();

        var users = repository.findAll();
        Assertions.assertThat(users).contains(userToSave);
    }
}