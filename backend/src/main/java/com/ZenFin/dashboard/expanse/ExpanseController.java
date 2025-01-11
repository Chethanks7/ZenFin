package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.api.ApiResponse;
import com.ZenFin.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@PreAuthorize("hasRole('USER')")
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpanseController {

  private final ExpenseService expenseService;



  @PostMapping("/test")
  public String test() throws Exception {
    System.err.println("Before cheching");
    return "tested";
  }


  @PostMapping("/saveExpanse")
  public ResponseEntity<ExpenseResponse> saveExpanse(
//    @Valid
//    @NotNull(message = "payload missing some data verify again")
    @RequestBody ExpenseDTO expense
  ) throws Exception {
    System.err.println(expense.getDate());
    return ResponseEntity.ok(expenseService.saveExpanse(expense));
  }

  @GetMapping("/all-expenses")
  public ResponseEntity<?> getAllResponses(
    @RequestParam(name = "page", defaultValue = "0", required = false) int page,
    @RequestParam(name = "size", defaultValue = "10", required = false) int size,
    Authentication connectedUser
  ) throws Exception {
    ApiResponse<?> res = expenseService.getAllExpenses(page, size, connectedUser);
    return ResponseEntity.status(res.getStatusCode()).body(res);
  }

 @DeleteMapping("/delete-expense-by-id")
  public ResponseEntity<String> deleteExpenseById(@RequestParam String id){
    return ResponseEntity.ok(expenseService.deleteExpenseById(id));
 }

}
