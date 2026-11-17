package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NoteRequestDto(
        @NotBlank(message = "Title is required")
        String title,
        String description
) {

}
