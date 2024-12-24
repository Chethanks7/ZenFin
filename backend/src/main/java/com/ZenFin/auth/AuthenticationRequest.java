package com.ZenFin.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {


    @NotEmpty(message = "email can't be empty")
    @Email(message = "email format is not valid")
    private String email;

    @NotEmpty(message = "password can't be empty")
    @Size(min = 8, message = "password should be minimum 8 character long")
    private String password;
}
