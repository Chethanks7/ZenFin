package com.ZenFin.dashboard.expanse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExpanseRepository extends JpaRepository<Expense,String> {

  @Query("""
        SELECT expanse
        FROM Expense expanse
        WHERE expanse.user.userId = :userId
        """)
  Page<Expense> findAllByUserId(Pageable pageable,@Param("userId") String userId);
}
