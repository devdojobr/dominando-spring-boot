package academy.devdojo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerPostRequest {
    @NotBlank(message = "The field 'name' is required")
    String name;
}
