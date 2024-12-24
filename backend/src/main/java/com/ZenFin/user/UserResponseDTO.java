package com.ZenFin.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private String userId ;

    private String fullName;

    private String email;


}
