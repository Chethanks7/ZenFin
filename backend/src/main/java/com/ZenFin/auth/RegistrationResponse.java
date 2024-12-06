package com.ZenFin.auth;

import com.ZenFin.user.UserRegistrationDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {

   private String message;

   private UserRegistrationDTO userRegistrationDTO;

}
