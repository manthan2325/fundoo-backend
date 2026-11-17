/*
 * Class Name : UserRepositoryTest
 *
 * Description:
 * Tests for UserRepository using an in-memory database.
 * Verifies save, findByEmail and the unique email constraint.
 */

package org.example.repository;

import org.example.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity buildUser(String email) {
        UserEntity user = new UserEntity();
        user.setFirstName("Rohan");
        user.setEmail(email);
        user.setPassword("encoded-password");
        return user;
    }

    /*
     * Test Case 1:
     * Saving a user generates an id.
     */
    @Test
    void shouldSaveUser() {

        UserEntity saved = userRepository.save(buildUser("rohan@gmail.com"));

        assertNotNull(saved.getId());
        assertEquals("rohan@gmail.com", saved.getEmail());
    }

    /*
     * Test Case 2:
     * findByEmail returns the user when the email exists.
     */
    @Test
    void shouldFindUserByEmail() {

        userRepository.save(buildUser("rohan@gmail.com"));

        Optional<UserEntity> found = userRepository.findByEmail("rohan@gmail.com");

        assertTrue(found.isPresent());
        assertEquals("Rohan", found.get().getFirstName());
    }

    /*
     * Test Case 3:
     * findByEmail returns empty when the email does not exist.
     */
    @Test
    void shouldReturnEmptyWhenEmailNotFound() {

        Optional<UserEntity> found = userRepository.findByEmail("nobody@gmail.com");

        assertTrue(found.isEmpty());
    }

    /*
     * Test Case 4:
     * Two users cannot share the same email (unique constraint).
     */
    @Test
    void shouldNotAllowDuplicateEmail() {

        userRepository.saveAndFlush(buildUser("same@gmail.com"));

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(buildUser("same@gmail.com")));
    }

    /*
     * Test Case 5:
     * Deleting a user removes it from the database.
     */
    @Test
    void shouldDeleteUser() {

        UserEntity saved = userRepository.save(buildUser("rohan@gmail.com"));

        userRepository.deleteById(saved.getId());

        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }
}