package academy.devdojo.repository;

import academy.devdojo.domain.Anime;
import academy.devdojo.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {

    List<Producer> findByName(String name);
}
