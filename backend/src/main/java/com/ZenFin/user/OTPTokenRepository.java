package com.ZenFin.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPTokenRepository extends JpaRepository<OTPToken, Integer> {

    Optional<OTPToken> findByOtp(String otp);

}
