package academy.devdojo.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
