package academy.devdojo.repository;


import academy.devdojo.commons.ProducerUtils;
import academy.devdojo.domain.Producer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerHardCodedRepositoryTest {
    @InjectMocks
    private ProducerHardCodedRepository repository;
    @Mock
    private ProducerData producerData;
    private List<Producer> producers;
    @InjectMocks
    private ProducerUtils producerUtils;

    @BeforeEach
    void init(){

        producers = producerUtils.newProducerList();

        BDDMockito.when(producerData.getProducers()).thenReturn(producers);
    }
    @Test
    @DisplayName("findAll() returns a list with all producers")
    @Order(1)
    void findAll_ReturnsAllProducers_WhenSuccessful(){
        var producers = repository.findAll();
        Assertions.assertThat(producers).hasSameElementsAs(this.producers);
    }

    @Test
    @DisplayName("findById() returns an object with given id")
    @Order(2)
    void findById_ReturnsAProducer_WhenSuccessful(){
        var producerOptional = repository.findById(3L);
        Assertions.assertThat(producerOptional).isPresent().contains(producers.get(2));
    }

    @Test
    @DisplayName("findByName() returns all producers when name is null")
    @Order(3)
    void findByName_ReturnsAllProducers_WhenNameIsNull(){
        var producers = repository.findByName(null);
        Assertions.assertThat(producers).hasSameElementsAs(this.producers);
    }

    @Test
    @DisplayName("findByName() returns list with filtered producers when name is not null")
    @Order(4)
    void findByName_ReturnsFilteredProducers_WhenNameIsNotNull(){
        var producers = repository.findByName("Ufotable");
        Assertions.assertThat(producers).hasSize(1).contains(this.producers.get(0));
    }

    @Test
    @DisplayName("findByName() returns empty list when no producer is found")
    @Order(5)
    void findByName_ReturnsEmptyListOfProducers_WhenNothingIsFound(){
        var producers = repository.findByName("XXXX");
        Assertions.assertThat(producers).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save() creates a producer")
    @Order(6)
    void save_CreatesProducer_WhenSuccessful(){
        var producerToSave = producerUtils.newProducerToSave();

        var producer = repository.save(producerToSave);

        Assertions.assertThat(producer)
                .isEqualTo(producerToSave)
                .hasNoNullFieldsOrProperties();

        var producers = repository.findAll();
        Assertions.assertThat(producers).contains(producerToSave);
    }

    @Test
    @DisplayName("delete() removes a producer")
    @Order(7)
    void delete_RemovesProducer_WhenSuccessful(){
        var producerToDelete = this.producers.get(0);
        repository.delete(producerToDelete);

        Assertions.assertThat(this.producers).doesNotContain(producerToDelete);
    }

    @Test
    @DisplayName("update() update a producer")
    @Order(8)
    void update_UpdateProducer_WhenSuccessful(){
        var producerToUpdate = this.producers.get(0);
        producerToUpdate.setName("Aniplex");

        repository.update(producerToUpdate);

        Assertions.assertThat(this.producers).contains(producerToUpdate);
        this.producers
                .stream()
                .filter(producer -> producer.getId().equals(producerToUpdate.getId()))
                .findFirst()
                .ifPresent(producer -> Assertions.assertThat(producer.getName()).isEqualTo(producerToUpdate.getName()));
    }
}