package academy.devdojo.service;

import academy.devdojo.domain.Producer;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.ProducerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static academy.devdojo.util.Constants.PRODUCER_NOT_FOUND_DELETED;
import static academy.devdojo.util.Constants.PRODUCER_NOT_FOUND_UPDATED;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final ProducerRepository repository;

    public List<Producer> findAll(String name) {
        return name == null ? repository.findAll() : repository.findByName(name);
    }

    public Producer save(Producer producer) {
        return repository.save(producer);
    }

    public Optional<Producer> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        var producer = findById(id).orElseThrow(() -> new NotFoundException(PRODUCER_NOT_FOUND_DELETED));
        repository.delete(producer);
    }

    public void update(Producer producerToUpdate) {
        findById(producerToUpdate.getId())
                .ifPresentOrElse(p -> repository.save(producerToUpdate), () -> {
                            throw new NotFoundException(PRODUCER_NOT_FOUND_UPDATED);
                        });

    }
}
