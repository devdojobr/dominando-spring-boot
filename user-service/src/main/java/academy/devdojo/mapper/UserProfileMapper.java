package academy.devdojo.mapper;

import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.response.UserProfileGetResponse;
import academy.devdojo.response.UserProfileUserGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfileMapper {
    @Mapping(source = "profile.id", target = "profileId")
    @Mapping(source = "profile.name", target = "profileName")
    UserProfileGetResponse convert(UserProfile userProfile);
    List<UserProfileGetResponse> toUserProfileGetResponse(List<UserProfile> userProfiles);

    List<UserProfileUserGetResponse> toUserProfileUserGetResponse(List<User> users);
}
