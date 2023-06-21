package academy.devdojo.repository;

import academy.devdojo.domain.Profile;
import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query("SELECT up from UserProfile up join fetch up.user u join fetch up.profile p")
    List<UserProfile> retrieveAll();

//    @EntityGraph(attributePaths = {"user","profile"})
    @EntityGraph(value = "fullUserProfile")
    List<UserProfile> findAll();

    @Query("SELECT up.user from UserProfile up where up.profile.id  = ?1")
    List<User> findAllUsersByProfileId(Long profileId);
}
