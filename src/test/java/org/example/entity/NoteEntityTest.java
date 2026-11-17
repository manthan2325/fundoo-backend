/*
 * Class Name : NoteTest
 *
 * Description:
 * Unit tests for the Note entity. Verifies default values,
 * getters/setters, state flags and the user association.
 * These tests do not require a database connection.
 */

package org.example.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteEntityTest {

    /*
     * Test Case 1:
     * Default constructor creates an empty Note
     * with all state flags false.
     */
    @Test
    void shouldCreateEmptyNote() {

        NoteEntity note = new NoteEntity();

        assertNotNull(note);
        assertNotNull(note.getId());
        assertNull(note.getTitle());
        assertNull(note.getDescription());
        assertNull(note.getUser());
        assertFalse(note.isPinned());
        assertFalse(note.isArchived());
        assertFalse(note.isTrashed());
    }

    /*
     * Test Case 2:
     * Getters return the values assigned through setters.
     */
    @Test
    void shouldSetAndGetNoteFields() {

        NoteEntity note = new NoteEntity();

        note.setId(10L);
        note.setTitle("Meeting");
        note.setDescription("Discuss project plan");

        assertEquals(10L, note.getId());
        assertEquals("Meeting", note.getTitle());
        assertEquals("Discuss project plan", note.getDescription());
    }

    /*
     * Test Case 3:
     * Pinned, archived and trashed flags can be set independently.
     */
    @Test
    void shouldSetStateFlags() {

        NoteEntity note = new NoteEntity();

        note.setPinned(true);
        note.setArchived(true);
        note.setTrashed(true);

        assertTrue(note.isPinned());
        assertTrue(note.isArchived());
        assertTrue(note.isTrashed());

        note.setPinned(false);

        assertFalse(note.isPinned());
        assertTrue(note.isArchived());
        assertTrue(note.isTrashed());
    }

    /*
     * Test Case 4:
     * A note can be linked to its owner (User).
     */
    @Test
    void shouldAssignUserToNote() {

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("rohan@gmail.com");

        NoteEntity note = new NoteEntity();
        note.setUser(user);

        assertNotNull(note.getUser());
        assertEquals(1L, note.getUser().getId());
        assertEquals("rohan@gmail.com", note.getUser().getEmail());
    }
}