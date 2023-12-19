package academy.devdojo.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CepErrorResponse(String name, String message, String type, List<CepInnerErrorResponse> errors) {
}

