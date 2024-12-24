package com.ZenFin.security;

import com.ZenFin.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOtpStatus {

    @Id
    private String userID ;

    private  Byte maxFailedAttempts;

    private LocalDateTime lockTime;

}
