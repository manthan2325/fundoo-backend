package org.example.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String message, int statusCode, LocalDateTime timestamp) {
}
