package com.ZenFin.auth;

import com.ZenFin.user.UserRegistrationDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RegistrationResponse> registration(
            @RequestBody
            @Valid
            RegistrationRequest registration
    ) throws MessagingException {
        service.register(registration);

        var userResponse = UserRegistrationDTO.builder()
                .fullName(registration.getFirstname()+" "+registration.getLastname())
                .email(registration.getEmail())
                .build();
        return ResponseEntity.ok(RegistrationResponse.builder()
                        .message("Registration successful! Please verify your email.")
                        .userRegistrationDTO(userResponse)
                        .build());
    }
}
