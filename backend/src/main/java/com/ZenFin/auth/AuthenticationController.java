package com.ZenFin.auth;

import com.ZenFin.user.User;
import com.ZenFin.user.UserRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RegistrationResponse> registration(
            @RequestBody
            @Valid
            RegistrationRequest registration
    ) throws Exception {
        User user = service.register(registration);

        var userResponse = UserRegistrationDTO.builder()
                .fullName(user.fullName())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
        return ResponseEntity.ok(RegistrationResponse.builder()
                        .message("Registration successful! Please verify your email.")
                        .userRegistrationDTO(userResponse)
                        .build());
    }

    @GetMapping("verify-otp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> verifyEmail(@RequestParam String otp,@RequestParam String userId) throws Exception {

        return  ResponseEntity.ok(service.verifyOtp(otp,userId));

    }

}
