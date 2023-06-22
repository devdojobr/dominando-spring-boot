package academy.devdojo.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfileGetResponse {

    public record UserProfileUser(Long id, String firstName) {
    }
    private Long id;
    private UserProfileUser user;
    private Long profileId;
    private String profileName;
}
