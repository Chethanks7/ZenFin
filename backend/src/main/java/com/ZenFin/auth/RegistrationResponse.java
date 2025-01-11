package com.ZenFin.auth;

import com.ZenFin.user.UserResponseDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {

   private String message;
   private String iv;
   private UserResponseDTO userRegistrationDTO;

}
