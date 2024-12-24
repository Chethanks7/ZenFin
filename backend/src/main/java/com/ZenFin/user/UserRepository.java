package com.ZenFin.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.email, u.lastEmailSentTime, u.resendAttempts \n"  +
            "FROM User u " +
            "WHERE u.userId = :userId ")
    Optional<User> findUserByUserId(@Param("userId") String userId);

}

