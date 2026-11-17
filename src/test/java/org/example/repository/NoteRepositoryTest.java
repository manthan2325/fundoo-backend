/*
 * Class Name : NoteRepositoryTest
 *
 * Description:
 * Tests for NoteRepository using an in-memory database.
 * Verifies save, delete and the three custom query methods.
 */

package org.example.repository;

import org.example.entity.NoteEntity;
import org.example.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity userA;
    private UserEntity userB;

    @BeforeEach
    void setUp() {
        userA = userRepository.save(buildUser("a@gmail.com"));
        userB = userRepository.save(buildUser("b@gmail.com"));
    }

    private UserEntity buildUser(String email) {
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setEmail(email);
        user.setPassword("encoded-password");
        return user;
    }

    private NoteEntity saveNote(UserEntity owner, String title,
                                boolean archived, boolean trashed) {
        NoteEntity note = new NoteEntity();
        note.setTitle(title);
        note.setDescription("desc");
        note.setArchived(archived);
        note.setTrashed(trashed);
        note.setUser(owner);
        return noteRepository.save(note);
    }

    /*
     * Test Case 1:
     * Saving a note generates an id and keeps its owner.
     */
    @Test
    void shouldSaveNote() {

        NoteEntity saved = saveNote(userA, "Shopping", false, false);

        assertNotNull(saved.getId());
        assertEquals("Shopping", saved.getTitle());
        assertEquals(userA.getId(), saved.getUser().getId());
    }

    /*
     * Test Case 2:
     * findByUserId returns only the notes of that user.
     */
    @Test
    void shouldFindNotesByUserId() {

        saveNote(userA, "A1", false, false);
        saveNote(userA, "A2", false, false);
        saveNote(userB, "B1", false, false);

        List<NoteEntity> notes = noteRepository.findByUserId(userA.getId());

        assertEquals(2, notes.size());
    }

    /*
     * Test Case 3:
     * findByUserId returns an empty list when the user has no notes.
     */
    @Test
    void shouldReturnEmptyListWhenUserHasNoNotes() {

        List<NoteEntity> notes = noteRepository.findByUserId(userA.getId());

        assertTrue(notes.isEmpty());
    }

    /*
     * Test Case 4:
     * Active notes exclude archived and trashed ones.
     */
    @Test
    void shouldFindOnlyActiveNotes() {

        saveNote(userA, "Active", false, false);
        saveNote(userA, "Archived", true, false);
        saveNote(userA, "Trashed", false, true);
        saveNote(userB, "Other user active", false, false);

        List<NoteEntity> notes =
                noteRepository.findByUserIdAndArchivedFalseAndTrashedFalse(userA.getId());

        assertEquals(1, notes.size());
        assertEquals("Active", notes.get(0).getTitle());
    }

    /*
     * Test Case 5:
     * Title search is case-insensitive and partial.
     */
    @Test
    void shouldSearchByTitleIgnoringCase() {

        saveNote(userA, "Grocery List", false, false);
        saveNote(userA, "Meeting", false, false);

        List<NoteEntity> notes =
                noteRepository.findByUserIdAndTitleContainingIgnoreCase(userA.getId(), "grocery");

        assertEquals(1, notes.size());
        assertEquals("Grocery List", notes.get(0).getTitle());
    }

    /*
     * Test Case 6:
     * Title search only returns notes of the given user.
     */
    @Test
    void shouldSearchOnlyWithinUsersNotes() {

        saveNote(userA, "Grocery A", false, false);
        saveNote(userB, "Grocery B", false, false);

        List<NoteEntity> notes =
                noteRepository.findByUserIdAndTitleContainingIgnoreCase(userA.getId(), "grocery");

        assertEquals(1, notes.size());
        assertEquals("Grocery A", notes.get(0).getTitle());
    }

    /*
     * Test Case 7:
     * Title search returns an empty list when nothing matches.
     */
    @Test
    void shouldReturnEmptyWhenNoTitleMatches() {

        saveNote(userA, "Meeting", false, false);

        List<NoteEntity> notes =
                noteRepository.findByUserIdAndTitleContainingIgnoreCase(userA.getId(), "xyz");

        assertTrue(notes.isEmpty());
    }

    /*
     * Test Case 8:
     * Deleting a note removes it from the database.
     */
    @Test
    void shouldDeleteNote() {

        NoteEntity note = saveNote(userA, "To delete", false, false);

        noteRepository.deleteById(note.getId());

        assertTrue(noteRepository.findById(note.getId()).isEmpty());
    }
}