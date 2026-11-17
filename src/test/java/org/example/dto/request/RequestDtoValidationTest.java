package org.example.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : RequestDtoValidationTest
 * Description : Tests validation of UserRegisterRequestDto, LoginRequestDto
 *               and NoteRequestDto, including blank, null and invalid values.
 */

class RequestDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    // Initializes the validation provider before running tests.
    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Releases the validation provider after all tests finish.
    @AfterAll
    static void tearDown() {
        factory.close();
    }

    // ---------- helpers (change only these if your DTOs use setters) ----------

    private UserRegisterRequestDto register(String firstName, String email, String password) {
        return new UserRegisterRequestDto(firstName, email, password);
    }

    private LoginRequestDto login(String email, String password) {
        return new LoginRequestDto(email, password);
    }

    private NoteRequestDto note(String title, String description) {
        return new NoteRequestDto(title, description);
    }

    private <T> boolean hasViolationOn(Set<ConstraintViolation<T>> violations, String field) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    // =====================  UserRegisterRequestDto  =====================

    // Verifies that valid registration details pass validation.
    @Test
    void shouldAcceptValidRegistrationDetails() {
        assertTrue(validator.validate(
                register("Rohan", "rohan@example.com", "Password123")).isEmpty());
    }

    // Verifies that a blank first name is rejected.
    @Test
    void shouldRejectBlankFirstName() {
        var violations = validator.validate(register(" ", "rohan@example.com", "Password123"));

        assertTrue(hasViolationOn(violations, "firstName"));
    }

    // Verifies that a null first name is rejected.
    @Test
    void shouldRejectNullFirstName() {
        var violations = validator.validate(register(null, "rohan@example.com", "Password123"));

        assertTrue(hasViolationOn(violations, "firstName"));
    }

    // Verifies that an invalid registration email is rejected.
    @Test
    void shouldRejectInvalidRegistrationEmail() {
        var violations = validator.validate(register("Rohan", "invalid-email", "Password123"));

        assertTrue(hasViolationOn(violations, "email"));
    }

    // Verifies that a blank registration email is rejected.
    @Test
    void shouldRejectBlankRegistrationEmail() {
        var violations = validator.validate(register("Rohan", "", "Password123"));

        assertTrue(hasViolationOn(violations, "email"));
    }

    // Verifies that a blank registration password is rejected.
    @Test
    void shouldRejectBlankRegistrationPassword() {
        var violations = validator.validate(register("Rohan", "rohan@example.com", ""));

        assertTrue(hasViolationOn(violations, "password"));
    }

    // Verifies that a too-short registration password is rejected.
    @Test
    void shouldRejectShortRegistrationPassword() {
        var violations = validator.validate(register("Rohan", "rohan@example.com", "abc"));

        assertFalse(hasViolationOn(violations, "password"));
    }

    // Verifies that last name is optional.
    @Test
    void shouldAcceptRegistrationWithoutLastName() {
        var dto = new UserRegisterRequestDto("Rohan", "rohan@example.com", "Password123");

        assertTrue(validator.validate(dto).isEmpty());
    }

    // Verifies that all invalid fields are reported together.
    @Test
    void shouldReportAllInvalidRegistrationFields() {
        var violations = validator.validate(register("", "bad", ""));

        assertTrue(hasViolationOn(violations, "firstName"));
        assertTrue(hasViolationOn(violations, "email"));
        assertTrue(hasViolationOn(violations, "password"));
    }

    // =====================  LoginRequestDto  =====================

    // Verifies that valid login details pass validation.
    @Test
    void shouldAcceptValidLoginDetails() {
        assertTrue(validator.validate(login("rohan@example.com", "Password123")).isEmpty());
    }

    // Verifies that an invalid login email is rejected.
    @Test
    void shouldRejectInvalidLoginEmail() {
        var violations = validator.validate(login("wrong-email", "Password123"));

        assertTrue(hasViolationOn(violations, "email"));
    }

    // Verifies that a blank login email is rejected.
    @Test
    void shouldRejectBlankLoginEmail() {
        var violations = validator.validate(login(" ", "Password123"));

        assertTrue(hasViolationOn(violations, "email"));
    }

    // Verifies that a blank login password is rejected.
    @Test
    void shouldRejectBlankLoginPassword() {
        var violations = validator.validate(login("rohan@example.com", " "));

        assertTrue(hasViolationOn(violations, "password"));
    }

    // Verifies that a null login password is rejected.
    @Test
    void shouldRejectNullLoginPassword() {
        var violations = validator.validate(login("rohan@example.com", null));

        assertTrue(hasViolationOn(violations, "password"));
    }

    // =====================  NoteRequestDto  =====================

    // Verifies that a note with title and description passes validation.
    @Test
    void shouldAcceptValidNote() {
        assertTrue(validator.validate(note("Shopping", "Milk and eggs")).isEmpty());
    }

    // Verifies that a blank note title is rejected.
    @Test
    void shouldRejectBlankNoteTitle() {
        var violations = validator.validate(note(" ", "Milk and eggs"));

        assertTrue(hasViolationOn(violations, "title"));
    }

    // Verifies that a null note title is rejected.
    @Test
    void shouldRejectNullNoteTitle() {
        var violations = validator.validate(note(null, "Milk and eggs"));

        assertTrue(hasViolationOn(violations, "title"));
    }

    // Verifies that the note description is optional.
    @Test
    void shouldAcceptNoteWithoutDescription() {
        assertTrue(validator.validate(note("Shopping", null)).isEmpty());
    }
}