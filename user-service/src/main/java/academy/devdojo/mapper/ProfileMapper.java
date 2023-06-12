package academy.devdojo.mapper;

import academy.devdojo.domain.Profile;
import academy.devdojo.domain.User;
import academy.devdojo.request.ProfilePostRequest;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {
    Profile toProfile(ProfilePostRequest request);

    ProfilePostResponse toProfilePostResponse(Profile profile);

    List<ProfileGetResponse> toProfileGetResponses(List<Profile> profiles);

}
