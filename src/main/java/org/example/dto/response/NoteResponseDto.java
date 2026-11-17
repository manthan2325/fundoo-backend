package org.example.dto.response;

public record NoteResponseDto(
        Long id,
        String title,
        String description,
        boolean pinned,
        boolean archived,
        boolean trashed
) {
}
