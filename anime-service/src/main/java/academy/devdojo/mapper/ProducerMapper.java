package academy.devdojo.mapper;

import academy.devdojo.domain.Producer;
import academy.devdojo.dto.ProducerGetResponse;
import academy.devdojo.dto.ProducerPostRequest;
import academy.devdojo.dto.ProducerPostResponse;
import academy.devdojo.dto.ProducerPutRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProducerMapper {
    Producer toProducer(ProducerPostRequest request);

    Producer toProducer(ProducerPutRequest request);

    ProducerPostResponse toProducerPostResponse(Producer producer);

    List<ProducerGetResponse> toProducerGetResponses(List<Producer> producers);
}
