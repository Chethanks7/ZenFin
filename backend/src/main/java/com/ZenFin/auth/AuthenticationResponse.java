package com.ZenFin.auth;


import com.ZenFin.user.UserResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @NotNull
    private String token;
    @NotNull
    private String message;
    @NotNull
    private HttpStatus status;

}
