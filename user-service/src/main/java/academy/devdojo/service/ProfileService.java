package academy.devdojo.service;

import academy.devdojo.domain.Profile;
import academy.devdojo.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository repository;

    public List<Profile> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Profile save(Profile profile) {
        return repository.save(profile);
    }

}
