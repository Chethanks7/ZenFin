package com.ZenFin.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    // Field for the user's first name with validation constraints.
    @NotEmpty(message = "first name can't be empty") // Ensures the field is not empty.
    @NotBlank(message = "first name can't be empty") // Ensures the field is not blank (no whitespace).
    private String firstname;

    // Field for the user's last name with validation constraints.
    @NotEmpty(message = "last name can't be empty") // Ensures the field is not empty.
    @NotBlank(message = "last name can't be empty") // Ensures the field is not blank.
    private String lastname;

    // Field for the user's password with validation constraints.
    @NotEmpty(message = "password can't be empty") // Ensures the field is not empty.
    @NotBlank(message = "password can't be empty") // Ensures the field is not blank.
    @Size(min = 8, message = "password should be minimum 8 character long") // Enforces a minimum length of 8 characters.
    private String password;

    // Field for the user's email with validation constraints.
    @Email(message = "not formated email") // Ensures the email is in a valid format.
    @NotEmpty(message = "email can't be empty") // Ensures the field is not empty.
    @NotBlank(message = "email can't be empty") // Ensures the field is not blank.
    private String email;
}
