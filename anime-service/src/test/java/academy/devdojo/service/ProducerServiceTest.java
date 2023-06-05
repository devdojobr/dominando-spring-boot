package academy.devdojo.service;

import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.domain.Producer;
import academy.devdojo.repository.ProducerHardCodedRepository;
import academy.devdojo.repository.ProducerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerServiceTest {
    @InjectMocks
    private ProducerService service;
    @Mock
    private ProducerRepository repository;
    private List<Producer> producers;
    @InjectMocks
    private ProducerUtils producerUtils;

    @BeforeEach
    void init(){
        producers = producerUtils.newProducerList();
    }

    @Test
    @DisplayName("findAll() returns a list with all producers")
    @Order(1)
    void findAll_ReturnsAllProducers_WhenSuccessful(){
        BDDMockito.when(repository.findAll()).thenReturn(this.producers);

        var producers = service.findAll(null);
        Assertions.assertThat(producers).hasSameElementsAs(this.producers);
    }

    @Test
    @DisplayName("findAll() returns a list with found producers when name is not null")
    @Order(2)
    void findAll_ReturnsFoundProducers_WhenNameIsPassedAndFound(){
        var name = "Ufotable";
        var producersFound = this.producers.stream().filter(producer -> producer.getName().equals(name)).toList();
        BDDMockito.when(repository.findByName(name)).thenReturn(producersFound);

        var producers = service.findAll(name);
        Assertions.assertThat(producers).hasSize(1).contains(producersFound.get(0));
    }

    @Test
    @DisplayName("findAll() returns an empty list when no producer is found by name")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNoNameIsFound(){
        var name = "x";
        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());

        var producers = service.findAll(name);

        Assertions.assertThat(producers).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById() returns a optional producer when id exists")
    @Order(4)
    void findById_ReturnsOptionalProducer_WhenIdExists(){
        var id = 1L;
        var producerExpected = this.producers.get(0);

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(producerExpected));

        var producerOptional = service.findById(id);

        Assertions.assertThat(producerOptional).isPresent().contains(producerExpected);
    }

    @Test
    @DisplayName("findById() returns a empty optional when id does not exists")
    @Order(5)
    void findById_ReturnsEmptyOptional_WhenIdDoesNotExists(){
        var id = 1L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        var producerOptional = service.findById(id);

        Assertions.assertThat(producerOptional).isEmpty();
    }

    @Test
    @DisplayName("save() creates producer")
    @Order(6)
    void save_CreatesProducer_WhenSuccessful(){
        var producerToSave = producerUtils.newProducerToSave();
        BDDMockito.when(repository.save(producerToSave)).thenReturn(producerToSave);
        var producer = service.save(producerToSave);

        Assertions.assertThat(producer)
                .isEqualTo(producerToSave)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("delete() removes a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful(){
        var id = 1L;
        var producerToDelete = this.producers.get(0);
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(producerToDelete));
        BDDMockito.doNothing().when(repository).delete(producerToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(id));
    }

    @Test
    @DisplayName("delete() throw ResponseStatusException when no producer is found")
    @Order(8)
    void delete_ThrowsResponseStatusException_WhenNoProducerIsFound(){
        var id = 1L;
        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(id))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("update() update a producer")
    @Order(9)
    void update_UpdateProducer_WhenSuccessful(){
        var id = 1L;
        var producerToUpdate = this.producers.get(0);
        producerToUpdate.setName("Aniplex");

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.of(producerToUpdate));
        BDDMockito.when(repository.save(producerToUpdate)).thenReturn(producerToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(producerToUpdate));
    }

    @Test
    @DisplayName("update() throw ResponseStatusException when no producer is found")
    @Order(10)
    void update_ThrowsResponseStatusException_WhenNoProducerIsFound(){
        var id = 1L;
        var producerToUpdate = this.producers.get(0);
        producerToUpdate.setName("Aniplex");

        BDDMockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(producerToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }
}