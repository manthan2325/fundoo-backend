package org.example.service.impl;

import org.example.dto.request.LoginRequestDto;
import org.example.dto.request.UserRegisterRequestDto;
import org.example.dto.response.LoginResponseDto;
import org.example.dto.response.UserResponseDto;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.example.util.JwtToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.InvalidParameterException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * Class Name  : UserServiceImplTest
 * Description : Unit tests for UserServiceImpl register and login.
 *               UserRepository, PasswordEncoder and JwtToken are mocked.
 */

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtToken jwtToken;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity(Long id, String firstName, String email, String password) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName(firstName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    private LoginRequestDto loginRequest(String email, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    // =====================  register  =====================

    // Verifies that a new user is saved and returned with the correct fields.
    @Test
    void shouldRegisterNewUser() {
        UserRegisterRequestDto request =
                new UserRegisterRequestDto("Rohan", "rohan@example.com", "secret123");

        when(userRepository.findByEmail("rohan@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity(1L, "Rohan", "rohan@example.com", "hashed-secret"));

        UserResponseDto response = userService.register(request);

        assertEquals(1L, response.getId());
        assertEquals("rohan@example.com", response.getEmail());
        assertEquals("Rohan", response.getFirstName());
    }

    // Verifies that the password is encoded before it is saved.
    @Test
    void shouldSaveEncodedPasswordNotPlainText() {
        UserRegisterRequestDto request =
                new UserRegisterRequestDto("Rohan", "rohan@example.com", "secret123");

        when(userRepository.findByEmail("rohan@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity(1L, "Rohan", "rohan@example.com", "hashed-secret"));

        userService.register(request);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        assertEquals("hashed-secret", captor.getValue().getPassword());
        assertNotEquals("secret123", captor.getValue().getPassword());
        assertEquals("Rohan", captor.getValue().getFirstName());
        assertEquals("rohan@example.com", captor.getValue().getEmail());
    }

    // Verifies that registering an existing email is rejected and nothing is saved.
    @Test
    void shouldRejectDuplicateEmail() {
        UserRegisterRequestDto request =
                new UserRegisterRequestDto("Rohan", "rohan@example.com", "secret123");

        when(userRepository.findByEmail("rohan@example.com"))
                .thenReturn(Optional.of(userEntity(1L, "Rohan", "rohan@example.com", "hashed-secret")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(request));

        assertEquals("Email already registered", ex.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    // =====================  login  =====================

    // Verifies that valid credentials return a token.
    @Test
    void shouldLoginWithCorrectPassword() {
        UserEntity stored = userEntity(1L, "Rohan", "rohan@example.com", "hashed-secret");

        when(userRepository.findByEmail("rohan@example.com")).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("secret123", "hashed-secret")).thenReturn(true);
        when(jwtToken.generateToken(1L)).thenReturn("jwt-token");

        LoginResponseDto response = userService.login(loginRequest("rohan@example.com", "secret123"));

        assertEquals("jwt-token", response.getToken());
        assertEquals("Login successfull", response.getMessage());
    }

    // Verifies that an unknown email is rejected and no token is created.
    @Test
    void shouldRejectLoginForUnknownUser() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        InvalidParameterException ex = assertThrows(InvalidParameterException.class,
                () -> userService.login(loginRequest("nobody@example.com", "secret123")));

        assertEquals("User not found", ex.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtToken, never()).generateToken(anyLong());
    }

    // Verifies that a wrong password is rejected and no token is created.
    @Test
    void shouldRejectLoginWithWrongPassword() {
        UserEntity stored = userEntity(1L, "Rohan", "rohan@example.com", "hashed-secret");

        when(userRepository.findByEmail("rohan@example.com")).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("wrong-pass", "hashed-secret")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login(loginRequest("rohan@example.com", "wrong-pass")));

        assertEquals("Password is wrong for the user", ex.getMessage());
        verify(jwtToken, never()).generateToken(anyLong());
    }
}