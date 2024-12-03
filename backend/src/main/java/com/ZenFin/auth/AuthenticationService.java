package com.ZenFin.auth;

import com.ZenFin.role.RoleRepository;
import com.ZenFin.user.User;
import com.ZenFin.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public void register(@Valid RegistrationRequest registration) {
        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role is not in database"));

        var user = User.builder()
                .firstName(registration.getFirstname())
                .lastName(registration.getLastname())
                .email(registration.getEmail())
                .password(registration.getPassword())// later add encoding the password
                .accountLocked(false)
                .roles(List.of(role))
                .build();

        userRepository.save(user);

    }
}
