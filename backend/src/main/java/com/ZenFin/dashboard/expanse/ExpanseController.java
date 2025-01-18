package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.api.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@PreAuthorize("hasRole('USER')")
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpanseController {

  private final ExpenseService expenseService;


  @PostMapping("/saveExpanse")
  public ResponseEntity<ExpenseResponse> saveExpanse(
    @Valid
    @NotNull(message = "payload missing some data verify again")
    @RequestBody ExpenseDTO expense
  )  {
    System.err.println(expense.getDate());
    assert expenseService != null;
    return ResponseEntity.ok(expenseService.saveExpanse(expense));
  }

  @GetMapping("/all-expenses")
  public ResponseEntity<?> getAllResponses(
    @RequestParam(name = "page", defaultValue = "0", required = false) int page,
    @RequestParam(name = "size", defaultValue = "10", required = false) int size
  ) {
    assert expenseService != null;
    ApiResponse<?> res = expenseService.getAllExpenses(page, size);
    return ResponseEntity.status(res.getStatusCode()).body(res);
  }

 @DeleteMapping("/delete-expense-by-id")
  public ResponseEntity<String> deleteExpenseById(@RequestParam String id){
   assert expenseService != null;
   return ResponseEntity.ok(expenseService.deleteExpenseById(id));
 }

 @GetMapping("/get-expenses-by-categories")
  public ResponseEntity<?> getExpenseByCategories(
   @RequestParam(name = "page", defaultValue = "0", required = false) int page,
   @RequestParam(name = "size", defaultValue = "10", required = false) int size,
   @RequestParam String category
 ){
   assert expenseService != null;
   ApiResponse<?> res = expenseService.getExpenseByCategories(page, size,category);
   return ResponseEntity.status(res.getStatusCode()).body(res);
 }

 @GetMapping("/monthly-summary")
  public ResponseEntity<MonthlyExpenseSummaryResponse> getMonthlyExpenseSummery(
    @RequestParam String userId,
    @RequestParam(name = "year", required = false) Integer year,
    @RequestParam(name = "month", required = false) Integer month,
    @RequestParam(name = "page", defaultValue = "0", required = false) int page,
  @RequestParam(name = "size", defaultValue = "10", required = false) int size
 ){
   assert expenseService != null;
   return ResponseEntity.ok(expenseService.getMonthlyExpenseSummery(userId,year, month,page,size));
 }

 @GetMapping("/expense-pdf-export")
  public void exportPdfExport(
    @RequestParam String userId,
    HttpServletResponse response
 ) throws Exception {
    expenseService.exportPdfExport(userId,response);
 }

 @GetMapping("get-top-three-expense")
  public ResponseEntity<List<TopThreeExpenseDTO>> topThreeExpense(@RequestParam String userId){
    return ResponseEntity.ok(expenseService.fetchTopThreeExpense(userId));

 }




}
