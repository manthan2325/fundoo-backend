package org.example.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : UserResponseDtoTest
 * Description : Tests UserResponseDto constructors, getters and setters.
 */

class UserResponseDtoTest {

    // Verifies that the all-args constructor initializes every field.
    @Test
    void shouldCreateUserResponseSuccessfully() {
        UserResponseDto dto = new UserResponseDto(
                1L,
                "rohan@example.com",
                "Rohan"
        );

        assertEquals(1L, dto.getId());
        assertEquals("rohan@example.com", dto.getEmail());
        assertEquals("Rohan", dto.getFirstName());
    }

    // Verifies that the default constructor leaves all fields empty.
    @Test
    void shouldCreateEmptyUserResponse() {
        UserResponseDto dto = new UserResponseDto();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getEmail());
        assertNull(dto.getFirstName());
    }

    // Verifies that setters update the fields.
    @Test
    void shouldUpdateUserResponseFields() {
        UserResponseDto dto = new UserResponseDto();

        dto.setId(2L);
        dto.setEmail("test@example.com");
        dto.setFirstName("Test User");

        assertEquals(2L, dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Test User", dto.getFirstName());
    }

    // Verifies that setters overwrite values set by the constructor.
    @Test
    void shouldOverwriteValuesSetByConstructor() {
        UserResponseDto dto = new UserResponseDto(
                1L,
                "old@example.com",
                "Old Name"
        );

        dto.setEmail("new@example.com");
        dto.setFirstName("New Name");

        assertEquals(1L, dto.getId());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("New Name", dto.getFirstName());
    }

    // Verifies that fields are independent of each other.
    @Test
    void shouldUpdateOnlyTheFieldThatWasSet() {
        UserResponseDto dto = new UserResponseDto();

        dto.setEmail("only@example.com");

        assertEquals("only@example.com", dto.getEmail());
        assertNull(dto.getId());
        assertNull(dto.getFirstName());
    }

    // Verifies that fields accept null values.
    @Test
    void shouldAllowNullValues() {
        UserResponseDto dto = new UserResponseDto(1L, "rohan@example.com", "Rohan");

        dto.setEmail(null);
        dto.setFirstName(null);

        assertNull(dto.getEmail());
        assertNull(dto.getFirstName());
        assertEquals(1L, dto.getId());
    }
}