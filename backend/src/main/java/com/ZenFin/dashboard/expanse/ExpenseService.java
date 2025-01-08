package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.api.ApiResponse;
import com.ZenFin.redis.RedisService;
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
  private final RedisService redisService ;


  public ExpenseResponse saveExpanse(ExpenseDTO expense) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) auth.getPrincipal();

    var expanseData = Expense.builder()
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .date(expense.getDate())
      .user(user)
      .recurring(expense.isRecurring())
      .recurrenceFrequency(expense.getRecurrenceFrequency())
      .nextDueDate(expense.getNextDueDate())
      .build();

    expanseRepository.save(expanseData);
    return ExpenseResponse.builder()
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .date(expense.getDate())
      .userId(user.getUserId())
      .build();

  }

  public ApiResponse<PageResponse<ExpenseResponse>> getAllExpenses(int page, int size, Authentication connectedUser) throws Exception {
    
    var resp = redisService.get("userId", User.class);
    String userId = null ;
    if(resp == null){
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      userId = ((User) auth.getPrincipal()).getUserId() ;  
      redisService.set(userId, User.class, 600L);
    }

      return null;
  }
}
