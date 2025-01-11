package com.ZenFin.auth;

import com.ZenFin.user.User;
import com.ZenFin.user.UserResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public String getTest(){
        return "test passed";
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RegistrationResponse> registration(
            @RequestBody
            @Valid
            RegistrationRequest registration
    ) throws Exception {
        return ResponseEntity.ok(service.register(registration));
    }

    @GetMapping("verify-otp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> verifyEmail(
            @RequestParam
            @NotBlank(message = "otp can't be empty")
            String otp
            ,
            @RequestParam
            @NotBlank(message = "email can't be empty")
            String userId) throws Exception {

        return ResponseEntity.ok(service.verifyOtp(otp, userId));

    }


    @PostMapping("resend-otp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> resendEmail(@RequestParam String userId) throws Exception {
        return ResponseEntity.ok(service.resendOtp(userId));
    }

    @PostMapping("verify-email")
    public ResponseEntity<EmailAuthResponse> verifyEmail(
            @RequestParam
            @Email(message = "not formated email")
            @NotBlank(message = "email can't be empty")
            String email
    )  {
        var response = service.verifyEmail(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody
            AuthenticationRequest request
    ) throws Exception {
        var response = service.authenticate(request);
        HttpStatus status = response instanceof AuthenticationResponse ?((AuthenticationResponse) response).getStatus() :((EmailAuthResponse) response).getStatus();
        return ResponseEntity.status(status).body(
                response
        );
    }
}
