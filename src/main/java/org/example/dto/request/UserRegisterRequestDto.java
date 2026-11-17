
package org.example.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * Class Name  : UserRegisterRequestDto
 * Description : Stores and validates the user registration
 *               details received from the client.
 * Author      :  Manthan Hanchate
 * Date        : 14/11/2026
 */

public class UserRegisterRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor for DTO object creation.
    public UserRegisterRequestDto() {
    }

    public UserRegisterRequestDto(String firstName, String email, String password) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }

    // Returns the first name.
    public String getFirstName() {
        return firstName;
    }

    // Sets the first name.
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Returns the email address.
    public String getEmail() {
        return email;
    }

    // Sets the email address.
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns the password.
    public String getPassword() {
        return password;
    }

    // Sets the password.
    public void setPassword(String password) {
        this.password = password;
    }
}