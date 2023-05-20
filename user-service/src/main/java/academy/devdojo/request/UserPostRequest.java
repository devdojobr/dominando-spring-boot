package academy.devdojo.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostRequest {
    private String firstName;
    private String lastName;
    private String email;
}
