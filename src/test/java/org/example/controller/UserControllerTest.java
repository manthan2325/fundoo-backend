package org.example.controller;

import org.example.dto.request.LoginRequestDto;
import org.example.dto.request.UserRegisterRequestDto;
import org.example.dto.response.LoginResponseDto;
import org.example.dto.response.UserResponseDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Class Name  : UserControllerTest
 * Description : Tests the register and login endpoints of UserController,
 *               including request validation. UserService is mocked.
 */

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // =====================  POST /api/users/register  =====================

    // Verifies that a valid registration returns 201 and the created user.
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        when(userService.register(any(UserRegisterRequestDto.class)))
                .thenReturn(new UserResponseDto(1L, "rohan@example.com", "Rohan"));

        String body = """
                {"firstName":"Rohan","email":"rohan@example.com","password":"secret123"}
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("rohan@example.com"))
                .andExpect(jsonPath("$.firstName").value("Rohan"));

        verify(userService).register(any(UserRegisterRequestDto.class));
    }

    // Verifies that a missing email is rejected before reaching the service.
    @Test
    void shouldRejectRegistrationWithoutEmail() throws Exception {
        String body = """
                {"firstName":"Rohan","password":"secret123"}
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
    }

    // Verifies that an invalid email format is rejected.
    @Test
    void shouldRejectRegistrationWithInvalidEmail() throws Exception {
        String body = """
                {"firstName":"Rohan","email":"invalid-email","password":"secret123"}
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
    }

    // Verifies that a blank first name is rejected.
    @Test
    void shouldRejectRegistrationWithBlankFirstName() throws Exception {
        String body = """
                {"firstName":" ","email":"rohan@example.com","password":"secret123"}
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
    }

    // Verifies that a blank password is rejected.
    @Test
    void shouldRejectRegistrationWithBlankPassword() throws Exception {
        String body = """
                {"firstName":"Rohan","email":"rohan@example.com","password":""}
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
    }

    // Verifies that a request with no body is rejected.
    @Test
    void shouldRejectRegistrationWithoutBody() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
    }

    // Verifies that non-JSON content is rejected with 415.
    @Test
    void shouldRejectRegistrationWithWrongContentType() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("firstName=Rohan"))
                .andExpect(status().isUnsupportedMediaType());

        verify(userService, never()).register(any());
    }

    // =====================  POST /api/users/login  =====================

    // Verifies that a valid login returns 200 and the token.
    @Test
    void shouldLoginSuccessfully() throws Exception {
        when(userService.login(any(LoginRequestDto.class)))
                .thenReturn(new LoginResponseDto("sample-token", "Login successful"));

        String body = """
                {"email":"rohan@example.com","password":"secret123"}
                """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample-token"))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(userService).login(any(LoginRequestDto.class));
    }

    // Verifies that an invalid login email is rejected.
    @Test
    void shouldRejectLoginWithInvalidEmail() throws Exception {
        String body = """
                {"email":"wrong-email","password":"secret123"}
                """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any());
    }

    // Verifies that a blank login password is rejected.
    @Test
    void shouldRejectLoginWithBlankPassword() throws Exception {
        String body = """
                {"email":"rohan@example.com","password":" "}
                """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any());
    }

    // Verifies that a login request with no body is rejected.
    @Test
    void shouldRejectLoginWithoutBody() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any());
    }
}