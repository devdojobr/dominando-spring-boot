package academy.devdojo.repository;

import academy.devdojo.domain.Profile;
import academy.devdojo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
