package academy.devdojo.repository;


import academy.devdojo.domain.Producer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerHardCodedRepositoryTest {
    @InjectMocks
    private ProducerHardCodedRepository repository;
    @Mock
    private ProducerData producerData;
    private List<Producer> producers;

    @BeforeEach
    void init(){
        var ufotable = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();
        var witStudio = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();
        var studioGhibli = Producer.builder().id(3L).name("Studio Ghibli").createdAt(LocalDateTime.now()).build();
        producers = List.of(ufotable, witStudio, studioGhibli);

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
    @DisplayName("findByName() returns list with filtered producers name is not null")
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
}