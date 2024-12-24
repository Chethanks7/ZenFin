package com.ZenFin.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpStatusRepository extends JpaRepository<UserOtpStatus, String> {

    Optional<UserOtpStatus> findByUserID(String userID);
}
