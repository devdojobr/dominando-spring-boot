package academy.devdojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostRequest {
    @NotBlank(message = "The field 'firstName' is required")
    @Schema(description = "User's first name", example = "Gojou")
    private String firstName;
    @NotBlank(message = "The field 'lastName' is required")
    @Schema(description = "User's last name", example = "Satoru")
    private String lastName;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,8}$", message = "The email format is not valid")
    @Schema(description = "User's email", example = "gojou.satoru@jujutsu.com")
    private String email;
    @NotBlank(message = "The field 'password' is required")
    private String password;
}
