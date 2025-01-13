package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.api.ApiResponse;
import com.ZenFin.dashboard.pdfService.PdfService;
import com.ZenFin.user.User;
import com.ZenFin.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

  private final ExpanseRepository expanseRepository;
  private final ExpanseMapper expanseMapper;
  private final EntityManager entityManager;
  private final UserRepository userRepository;
  private final PdfService pdfService ;


  public ExpenseResponse saveExpanse(ExpenseDTO expense) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var user = ((User) auth.getPrincipal());

    Expense expenseData = expanseMapper.toExpanse(expense);
    User mergerUser = entityManager.merge(user);
    expenseData.setUser(mergerUser);
    saveExpenseAsync(expenseData);

    return ExpenseResponse.builder()
      .amount(expense.getAmount())
      .id(expenseData.getId())
      .category(expense.getCategory().toLowerCase())
      .date(expense.getDate())
      .userId(user.getUserId())
      .build();
  }

  @Async
  public void saveExpenseAsync(Expense expenseData) {
    expanseRepository.save(expenseData);
    CompletableFuture.completedFuture(null);
  }


  public ApiResponse<?> getAllExpenses(int page, int size) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var userId = ((User) auth.getPrincipal()).getUserId();


    ApiResponse<PageResponse<ExpenseResponse>> body = new ApiResponse<>();

    Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
    Page<Expense> expenses = expanseRepository.findAllByUserId(pageable, userId);


    List<ExpenseResponse> responses = expenses
      .stream()
      .map(expanseMapper::toExpenseResponse)
      .toList();

    if (responses.isEmpty()) {
      return ApiResponse.builder()
        .message("no expenses found")
        .statusCode(HttpStatus.OK)
        .data(null)
        .build();
    }
    PageResponse<ExpenseResponse> expanses = new PageResponse<>();
    expanses.setContents(responses);
    expanses.setSize(size);
    expanses.setPage(page);
    expanses.setStatus(HttpStatus.OK);
    body.setData(expanses);
    body.setMessage("All expenses");
    body.setStatusCode(HttpStatus.OK);

    return body;
  }

  public String deleteExpenseById(String id) {
    var expanse = expanseRepository.findById(id).orElseThrow(
      () -> new IllegalStateException("expense is not present in this id")
    );

    if (ChronoUnit.HOURS.between(expanse.getCreatedTime(), LocalDateTime.now()) >= 24)
      throw new IllegalStateException("Expenses older than 24 hours cannot be deleted.");

    expanseRepository.delete(expanse);
    return "expense has been deleted successfully";

  }

  public ApiResponse<?> getExpenseByCategories(int page, int size, String category) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    var userId = ((User) auth.getPrincipal()).getUserId();

    ApiResponse<PageResponse<ExpenseResponse>> body = new ApiResponse<>();
    Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
    Page<Expense> expenses = expanseRepository.findExpanseByCategory(pageable, userId, category.toLowerCase());

    List<ExpenseResponse> categoryExpenses = expenses.stream()
      .map(expanseMapper::toExpenseResponse)
      .toList();

    if (categoryExpenses.isEmpty()) {
      return ApiResponse.builder()
        .message("no expenses found in this category")
        .statusCode(HttpStatus.OK)
        .data(null)
        .build();
    }

    PageResponse<ExpenseResponse> expanses = new PageResponse<>();
    expanses.setContents(categoryExpenses);
    expanses.setSize(size);
    expanses.setPage(page);
    expanses.setStatus(HttpStatus.OK);
    body.setData(expanses);
    body.setMessage("All expenses of category :" + category);
    body.setStatusCode(HttpStatus.OK);

    return body;
  }

  public MonthlyExpenseSummaryResponse getMonthlyExpenseSummery(String userId, Integer year, Integer month, int page, int size) {
    if (month == null) {
      Month val = LocalDate.now().getMonth();
      month = val.getValue();
    }
    if (year == null) {
      year = LocalDate.now().getYear();
    }
    Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
    List<Expense> expenseSummaries = expanseRepository.findAllMonthlyExpenseByUserId(pageable, userId, month, year);
    Map<String, BigDecimal> monthlyExpensesByCategories = getTotalAmountOfCategories(expenseSummaries);
    List<MonthlyExpenseSummary> summaries = getAllCategoriesMonthlySummary(monthlyExpensesByCategories, month, year);

    return MonthlyExpenseSummaryResponse.builder()
      .userId(userId)
      .summaries(summaries)
      .build();
  }

  private List<MonthlyExpenseSummary> getAllCategoriesMonthlySummary(Map<String, BigDecimal> monthlyExpensesByCategories, Integer month, Integer year) {
    List<MonthlyExpenseSummary> list = new ArrayList<>();
    for (String category : monthlyExpensesByCategories.keySet()) {
      list.add(MonthlyExpenseSummary.builder()
        .category(category)
        .totalAmount(monthlyExpensesByCategories.get(category))
        .month(month)
        .year(year)
        .build());
    }
    return list;
  }


  private Map<String, BigDecimal> getTotalAmountOfCategories(List<Expense> expenseSummaries) {
    Map<String, BigDecimal> expenseMap = new HashMap<>();
    for (Expense e : expenseSummaries) {
      String category = e.getCategory();
      if (!expenseMap.containsKey(category)) {
        expenseMap.put(category, e.getAmount());
      } else {
        expenseMap.put(e.getCategory(), expenseMap.get(category).add(e.getAmount()));
      }
    }
    return expenseMap;
  }

  public void exportPdfExport(String userId, HttpServletResponse response) throws Exception {

    response.setContentType("application/pdf");

    String headerValue = "attachment; filename=expense_report.pdf";
    response.setHeader("Content description", headerValue);
    List<Expense> expenses = fetchAllExpanse(userId);
    pdfService.exportExpense(response,expenses);

  }

  @Transactional
  private List<Expense> fetchAllExpanse(String userId) {
    return userRepository.findUserExpensesByUserId(userId);
  }

}
