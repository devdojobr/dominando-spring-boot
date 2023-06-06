package academy.devdojo.mapper;

import academy.devdojo.domain.Producer;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.request.ProducerPutRequest;
import academy.devdojo.response.ProducerGetResponse;
import academy.devdojo.response.ProducerPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
