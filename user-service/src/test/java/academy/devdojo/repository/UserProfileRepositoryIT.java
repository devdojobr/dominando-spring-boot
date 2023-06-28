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
class UserProfileRepositoryIT extends IntegrationTestContainers {
    @Autowired
    private UserProfileRepository repository;
    @Autowired
    private UserUtils userUtils;

    @Test
    @DisplayName("findAllUsersByProfileId() returns a list with all users by profile id")
    @Order(1)
    @Sql("/sql/init_user_profile_2_users_1_profile.sql")
    void findAllUsersByProfileId_ReturnsAllUsersByProfileId_WhenSuccessful() {
        var profileId = 999L;
        var users = repository.findAllUsersByProfileId(profileId);
        Assertions.assertThat(users).isNotEmpty()
                .hasSize(2)
                .doesNotContainNull();

        users.forEach(user -> Assertions.assertThat(user).hasNoNullFieldsOrProperties());
    }
}