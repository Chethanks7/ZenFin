package com.ZenFin.dashboard.expanse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpanseRepository extends JpaRepository<Expense, String> {

  @Query("""
    SELECT expanse
    FROM Expense expanse
    WHERE expanse.user.userId = :userId
    """)
  Page<Expense> findAllByUserId(Pageable pageable, @Param("userId") String userId);

  @Query("""
    SELECT e FROM Expense e
    WHERE e.category = :category AND e.user.userId = :userId
    ORDER BY e.date DESC
    """)
  Page<Expense> findExpanseByCategory(Pageable pageable, @Param("userId") String userId, @Param("category") String category);

//  @Query("SELECT new com.ZenFin.dashboard.expanse.MonthlyExpenseSummary(" +
//    "e.category, " +
//    "SUM(e.amount), " +
//    "EXTRACT(MONTH FROM e.date), " +
//    "EXTRACT(YEAR FROM e.date)) " +
//    "FROM Expense e " +
//    "WHERE e.user.userId = :userId " +
//    "AND EXTRACT(MONTH FROM e.date) = :month " +
//    "AND EXTRACT(YEAR FROM e.date) = :year " +
//    "GROUP BY e.category, EXTRACT(MONTH FROM e.date), EXTRACT(YEAR FROM e.date) " +
//    "ORDER BY EXTRACT(YEAR FROM e.date) DESC, EXTRACT(MONTH FROM e.date) DESC")
//  List<MonthlyExpenseSummary> findAllMonthlyExpenseByUserId(
//    @Param("userId") String userId,
//    @Param("month") Integer month,
//    @Param("year") Integer year);

  @Query("SELECT e  " +
    "FROM Expense e WHERE e.user.userId = :userId AND EXTRACT(MONTH FROM e.date) = :month " +
    "AND EXTRACT(YEAR FROM e.date) = :year ")
  List<Expense> findAllMonthlyExpenseByUserId(
    Pageable pageable,
    @Param("userId") String userId,
    @Param("month") Integer month,
    @Param("year") Integer year);

}
