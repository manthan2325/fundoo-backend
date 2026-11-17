package org.example.service.impl;

import org.example.dto.request.LoginRequestDto;
import org.example.dto.request.UserRegisterRequestDto;
import org.example.dto.response.LoginResponseDto;
import org.example.dto.response.UserResponseDto;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // ---------- register ----------

    @Test
    @DisplayName("register: saves new user and returns response DTO")
    void register_success() {
        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("John");
        request.setEmail("john@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        UserEntity saved = new UserEntity();
        saved.setId(1L);
        saved.setFirstName("John");
        saved.setEmail("john@example.com");
        saved.setPassword("secret123");
        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

        UserResponseDto response = userService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("john@example.com", response.getEmail());

        // verify the entity passed to save() was built correctly
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity toSave = captor.getValue();
        assertEquals("John", toSave.getFirstName());
        assertEquals("john@example.com", toSave.getEmail());
        assertEquals("secret123", toSave.getPassword());
    }

    @Test
    @DisplayName("register: throws when email already registered")
    void register_emailAlreadyExists() {
        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("John");
        request.setEmail("john@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(new UserEntity()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request));

        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // ---------- login ----------

    @Test
    @DisplayName("login: returns token when user exists")
    void login_success() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("john@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(new UserEntity()));

        LoginResponseDto response = userService.login(request);

        assertNotNull(response);
        assertEquals("SampleToken", response.getToken());
        assertEquals("Login successfull", response.getMessage());
    }

    @Test
    @DisplayName("login: throws when user not found")
    void login_userNotFound() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("missing@example.com");
        request.setPassword("whatever");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        InvalidParameterException ex = assertThrows(
                InvalidParameterException.class,
                () -> userService.login(request));

        assertEquals("User not found", ex.getMessage());
    }
}