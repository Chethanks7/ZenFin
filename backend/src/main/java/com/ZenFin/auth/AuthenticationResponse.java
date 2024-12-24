package com.ZenFin.auth;


import com.ZenFin.user.UserResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse extends EmailAuthResponse {

    @NotNull
    private String token;
    @NotNull
    private UserResponseDTO user;


}
