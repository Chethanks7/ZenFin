package com.ZenFin.user;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private String ivParameterSpec;

    @Column(nullable = false)
    private LocalDateTime expireTime;

    private Byte noOfAttempts;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}