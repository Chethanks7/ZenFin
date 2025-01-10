package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.api.ApiResponse;
import com.ZenFin.user.User;
import com.ZenFin.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

  private final ExpanseRepository expanseRepository;
  private final UserRepository userRepository ;
  private final ExpanseMapper expanseMapper ;
  private final EntityManager entityManager ;


  public ExpenseResponse saveExpanse(ExpenseDTO expense) throws Exception {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var user = ((User) auth.getPrincipal());

    // Fetch the User from the database to ensure it's managed
//    var user = userRepository.findById(userId)
//      .orElseThrow(() -> new RuntimeException("User not found"));
    Expense expenseData = expanseMapper.toExpanse(expense);
    User mergerUser = entityManager.merge(user);
    expenseData.setUser(mergerUser);
    saveExpenseAsync(expenseData);

    return ExpenseResponse.builder()
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .date(expense.getDate())
      .userId(user.getUserId())
      .build();
  }
  @Async
  public void saveExpenseAsync(Expense expenseData) {
    expanseRepository.save(expenseData);
    CompletableFuture.completedFuture(null);
  }


  public ApiResponse<?> getAllExpenses(int page, int size, Authentication connectedUser) throws Exception {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      var userId  = ((User) auth.getPrincipal()).getUserId() ;


    ApiResponse<PageResponse<ExpenseResponse>> body =  new ApiResponse<>();

    Pageable pageable =  PageRequest.of(page,size, Sort.by("date").descending());
    Page<Expense> expenses = expanseRepository.findAllByUserId(pageable,userId);


    List<ExpenseResponse> responses = expenses
      .stream()
      .map(expanseMapper::toExpenseResponse)
      .toList();

    if(responses.isEmpty()) {
      return ApiResponse.builder()
        .message("no expenses found")
        .statusCode(HttpStatus.OK)
        .data(null)
        .build();
    }
    PageResponse<ExpenseResponse> expanses= new PageResponse<>();
    expanses.setContents(responses);
    expanses.setSize(size);
    expanses.setPage(page);
    expanses.setStatus(HttpStatus.OK);
    body.setData(expanses);
    body.setMessage("All expenses");
    body.setStatusCode(HttpStatus.OK);

    return body;
  }
}
