package com.ZenFin.security;

import com.ZenFin.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpStatusRepository extends JpaRepository<UserOtpStatus, String> {

    UserOtpStatus findByUserID(String userID);
}
