package com.ZenFin.dashboard.expanse;

import com.ZenFin.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

  private final ExpanseRepository expanseRepository;


  public ExpenseResponse saveExpanse(ExpenseDTO expense) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) auth.getPrincipal();

    var expanseData = Expense.builder()
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .date(expense.getDate())
      .user(user)
      .recurring(true)
      .build();
    expanseRepository.save(expanseData);
    return ExpenseResponse.builder()
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .date(expense.getDate())
      .userId(user.getUserId())
      .build();

  }
}
