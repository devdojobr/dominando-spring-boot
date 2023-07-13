package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserGetResponse {
    @Schema(description = "User's id", example = "2109")
    private Long id;
    @Schema(description = "User's first name", example = "Gojou")
    private String firstName;
    @Schema(description = "User's last name", example = "Satoru")
    private String lastName;
    @Schema(description = "User's email", example = "gojou.satoru@jujutsu.com")
    private String email;
}
