/*
 * Class Name : UserTest
 *
 * Description:
 * Unit tests for the User entity. Verifies default constructor
 * behaviour, getters/setters and the notes list.
 * These tests do not require a database connection.
 */

package org.example.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    /*
     * Test Case 1:
     * Default constructor creates an empty User,
     * and the notes list is initialized (not null).
     */
    @Test
    void shouldCreateEmptyUser() {

        UserEntity user = new UserEntity();

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNotNull(user.getNotes());
        assertTrue(user.getNotes().isEmpty());
    }

    /*
     * Test Case 2:
     * Getters return the values assigned through setters.
     */
    @Test
    void shouldSetAndGetUserFields() {

        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setFirstName("Rohan");
        user.setEmail("rohan@gmail.com");
        user.setPassword("password123");

        assertEquals(1L, user.getId());
        assertEquals("Rohan", user.getFirstName());
        assertEquals("rohan@gmail.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    /*
     * Test Case 3:
     * A note can be added to the user's notes list.
     */
    @Test
    void shouldHoldNotesInList() {

        UserEntity user = new UserEntity();
        NoteEntity note = new NoteEntity();
        note.setTitle("Shopping");

        user.getNotes().add(note);

        assertEquals(1, user.getNotes().size());
        assertEquals("Shopping", user.getNotes().get(0).getTitle());
    }

    /*
     * Test Case 4:
     * setNotes replaces the existing notes list.
     */
    @Test
    void shouldReplaceNotesList() {

        UserEntity user = new UserEntity();
        NoteEntity note = new NoteEntity();
        note.setTitle("Work");

        user.setNotes(java.util.List.of(note));

        assertEquals(1, user.getNotes().size());
        assertSame(note, user.getNotes().get(0));
    }
}