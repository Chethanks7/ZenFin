package com.ZenFin.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    private String userId ;

    private String fullName;

    private String email;


}
