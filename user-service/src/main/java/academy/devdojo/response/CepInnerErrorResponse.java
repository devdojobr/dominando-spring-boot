package academy.devdojo.response;

import lombok.Builder;

@Builder
public record CepInnerErrorResponse(String name, String message, String service) {
}
