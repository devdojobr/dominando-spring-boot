package academy.devdojo.service;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.domain.User;
import academy.devdojo.exception.EmailAlreadyExistsException;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository repository;
    private List<User> users;
    @InjectMocks
    private UserUtils userUtils;

    @BeforeEach
    void init() {
        users = userUtils.newUserList();
    }

    @Test
    @DisplayName("findAll() returns a list with all users")
    @Order(1)
    void findAll_ReturnsAllUsers_WhenSuccessful() {
        BDDMockito.when(repository.findAll()).thenReturn(this.users);

        var users = service.findAll();
        assertThat(users).hasSameElementsAs(this.users);
    }

    @Test
    @DisplayName("findById() returns a optional user when id exists")
    @Order(2)
    void findById_ReturnsOptionalUser_WhenIdExists() {
        var id = 1L;
        var userExpected = this.users.get(0);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(userExpected));

        var userOptional = service.findById(id);

        assertThat(userOptional).isEqualTo(userExpected);
    }

    @Test
    @DisplayName("findById() throw NotFoundException when no user is found")
    @Order(3)
    void findById_ThrowsNotFoundException_WhenNoUserIsFound() {
        var id = 1L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Assertions
                .assertThatException()
                .isThrownBy(() -> service.findById(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("save() creates user")
    @Order(4)
    void save_CreatesUser_WhenSuccessful() {
        var userToSave = userUtils.newUserToSave();

        BDDMockito.when(repository.findByEmail(userToSave.getEmail())).thenReturn(Optional.empty());
        BDDMockito.when(repository.save(userToSave)).thenReturn(userToSave);
        var user = service.save(userToSave);

        Assertions.assertThat(user)
                .isEqualTo(userToSave)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete() removes an user")
    @Order(5)
    void delete_RemovesUser_WhenSuccessful() {
        var id = 1L;
        var userToDelete = this.users.get(0);
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(userToDelete));
        BDDMockito.doNothing().when(repository).delete(userToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(id));
    }

    @Test
    @DisplayName("delete() throw NotFoundException when no user is found")
    @Order(6)
    void delete_ThrowsNotFoundException_WhenNoUserIsFound() {
        var id = 1L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("update() update an user")
    @Order(7)
    void update_UpdateUser_WhenSuccessful() {
        var id = 1L;
        var userToUpdate = this.users.get(0).withFirstName("Astah");
        var savedUser = this.users.get(0);

        BDDMockito.when(repository.findByEmail(userToUpdate.getEmail())).thenReturn(Optional.of(savedUser));
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(userToUpdate));
        BDDMockito.when(repository.save(userToUpdate)).thenReturn(userToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(userToUpdate));
    }

    @Test
    @DisplayName("update() throw NotFoundException when no user is found")
    @Order(8)
    void update_ThrowsNotFoundException_WhenNoUserIsFound() {
        var id = 1L;
        var userToUpdate = this.users.get(0).withFirstName("Naruto");

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(userToUpdate))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("update() throw EmailAlreadyExistsException when email belongs to another user")
    @Order(9)
    void update_EmailAlreadyExistsException_WhenEmailBelongsToAnotherUser() {
        var id = 1L;
        var userToUpdate01 = this.users.get(0).withFirstName("Naruto");
        var userToUpdate02 = this.users.get(1).withEmail(userToUpdate01.getEmail());

        BDDMockito.when(repository.findByEmail(userToUpdate01.getEmail())).thenReturn(Optional.of(userToUpdate02));
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(userToUpdate01));

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(userToUpdate01))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @DisplayName("save() throw EmailAlreadyExistsException when email already exists")
    @Order(10)
    void save_EmailAlreadyExistsException_WhenEmailAlreadyExists() {
        var userToSave = userUtils.newUserToSave();
        var savedUser = this.users.get(0).withEmail(userToSave.getEmail());

        BDDMockito.when(repository.findByEmail(userToSave.getEmail())).thenReturn(Optional.of(savedUser));

        Assertions.assertThatException()
                .isThrownBy(() -> service.save(userToSave))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
}