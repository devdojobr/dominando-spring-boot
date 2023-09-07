package academy.devdojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnimePostRequest {
    @NotBlank(message = "The field 'name' is required")
    @Schema(example = "Name of Anime to update")
    String name;
}
