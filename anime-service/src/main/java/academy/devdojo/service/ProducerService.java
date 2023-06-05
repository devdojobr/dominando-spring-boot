package academy.devdojo.service;

import academy.devdojo.domain.Producer;
import academy.devdojo.exception.NotFoundException;
import academy.devdojo.repository.ProducerHardCodedRepository;
import academy.devdojo.repository.ProducerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        var producer = findById(id).orElseThrow(() -> new NotFoundException("Producer not found to be deleted"));
        repository.delete(producer);
    }

    public void update(Producer producerToUpdate) {
        var producer = findById(producerToUpdate.getId())
                .orElseThrow(() -> new NotFoundException("Producer not found to be updated"));
        producerToUpdate.setCreatedAt(producer.getCreatedAt());
        repository.save(producerToUpdate);
    }
}
