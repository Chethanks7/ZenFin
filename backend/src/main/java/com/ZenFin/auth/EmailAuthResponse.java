package com.ZenFin.auth;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

@Getter
@Setter()
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthResponse {

    private String email;
    @NotNull
    private String message;
    @NotNull
    private HttpStatus status;
}
