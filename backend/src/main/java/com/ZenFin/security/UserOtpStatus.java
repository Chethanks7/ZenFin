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

    @Column(unique = true)
    private  Byte maxFailedAttempts;

    @Column(unique = true, updatable = false)
    private LocalDateTime lockTime;

}
