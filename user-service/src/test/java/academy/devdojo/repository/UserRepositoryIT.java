package academy.devdojo.repository;

import academy.devdojo.commons.UserUtils;
import academy.devdojo.config.IntegrationTestContainers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserUtils.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryIT extends IntegrationTestContainers {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("save() creates an user")
    @Order(1)
    void save_CreatesUser_WhenSuccessful() {
        var userToSave = userUtils.newUserToSave();
        var user = repository.save(userToSave);
        Assertions.assertThat(user).hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("findAll() returns a list with all users")
    @Order(2)
    @Sql("/sql/user/init_one_user.sql")
    void findAll_ReturnsAllUsers_WhenSuccessful() {
        var users = repository.findAll();
        Assertions.assertThat(users).isNotEmpty();
    }


}