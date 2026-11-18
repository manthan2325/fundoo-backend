package org.example.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : GlobalExceptionHandlerTest
 * Description : Tests that GlobalExceptionHandler maps each exception
 *               to the right HTTP status and error body.
 */

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // Used only to build a MethodParameter for the validation exception.
    @SuppressWarnings("unused")
    private void dummy(String value) {
    }

    // =====================  404 Not Found  =====================

    // Verifies that a missing user returns 404 with the message.
    @Test
    void shouldReturn404ForUserNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new UserNotFoundException("User not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().message());
        assertEquals(404, response.getBody().statusCode());
        assertNotNull(response.getBody().timestamp());
    }

    // Verifies that a missing note returns 404 with the message.
    @Test
    void shouldReturn404ForNoteNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new NoteNotFoundException("Note not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Note not found", response.getBody().message());
        assertEquals(404, response.getBody().statusCode());
    }

    // =====================  409 Conflict  =====================

    // Verifies that a duplicate user returns 409 with the message.
    @Test
    void shouldReturn409ForUserAlreadyExists() {
        ResponseEntity<ErrorResponse> response =
                handler.handleConflict(new UserAlreadyExistsException("Email already registered"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already registered", response.getBody().message());
        assertEquals(409, response.getBody().statusCode());
    }

    // =====================  401 Unauthorized  =====================

    // Verifies that invalid credentials return 401.
    @Test
    void shouldReturn401ForInvalidCredentials() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorized(new InValidCredentialsException("Invalid credentials"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().message());
        assertEquals(401, response.getBody().statusCode());
    }

    // Verifies that an unauthorized request returns 401.
    @Test
    void shouldReturn401ForUnauthorized() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorized(new UnAuthorizedException("Invalid or expired token"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid or expired token", response.getBody().message());
        assertEquals(401, response.getBody().statusCode());
    }

    // =====================  400 Validation  =====================

    // Verifies that one field error is reported as "field: message".
    @Test
    void shouldReturn400WithSingleFieldError() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dto");
        bindingResult.addError(new FieldError("dto", "email", "Email is required"));

        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(validationException(bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("email: Email is required", response.getBody().message());
        assertEquals(400, response.getBody().statusCode());
    }

    // Verifies that several field errors are joined with a comma.
    @Test
    void shouldReturn400WithAllFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dto");
        bindingResult.addError(new FieldError("dto", "firstName", "First name is required"));
        bindingResult.addError(new FieldError("dto", "password", "Password is required"));

        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(validationException(bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("firstName: First name is required, password: Password is required",
                response.getBody().message());
    }

    // =====================  500 Generic  =====================

    // Verifies that unexpected errors return 500 with a generic message.
    @Test
    void shouldReturn500ForUnexpectedException() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGeneric(new RuntimeException("database password is root"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody().message());
        assertEquals(500, response.getBody().statusCode());
    }

    // Verifies that internal error details are not exposed to the client.
    @Test
    void shouldNotLeakInternalMessageOn500() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGeneric(new IllegalStateException("secret internal detail"));

        assertFalse(response.getBody().message().contains("secret internal detail"));
    }

    // ---------- helper ----------

    private MethodArgumentNotValidException validationException(BeanPropertyBindingResult bindingResult)
            throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class), 0);
        return new MethodArgumentNotValidException(parameter, bindingResult);
    }
}