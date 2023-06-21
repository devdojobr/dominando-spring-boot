package academy.devdojo.service;

import academy.devdojo.domain.Profile;
import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.repository.ProfileRepository;
import academy.devdojo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository repository;

    public List<UserProfile> findAll() {
        return repository.findAll();
    }

    public List<User> findAllUsersByProfileId(Long profileId) {
        return repository.findAllUsersByProfileId(profileId);
    }

}
