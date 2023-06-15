package academy.devdojo.repository;

import academy.devdojo.domain.Profile;
import academy.devdojo.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
