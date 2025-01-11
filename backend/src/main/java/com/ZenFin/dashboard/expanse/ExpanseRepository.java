package com.ZenFin.dashboard.expanse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpanseRepository extends JpaRepository<Expense,String> {

  @Query("""
        SELECT expanse
        FROM Expense expanse
        WHERE expanse.user.userId = :userId
        """)
  Page<Expense> findAllByUserId(Pageable pageable,@Param("userId") String userId);

  @Query("""
    SELECT e FROM Expense e
    WHERE e.category = :category AND e.user.userId = :userId
    ORDER BY e.date DESC
    """)
  Page<Expense> findExpanseByCategory(Pageable pageable, String userId, String category);
}
