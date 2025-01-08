package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/expense")
public class ExpanseController {

  private ExpenseService expenseService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/saveExpanse")
  public ResponseEntity<ExpenseResponse> saveExpanse(
    @Valid
    @NotNull(message = "payload missing some data verify again")
    @RequestBody ExpenseDTO expense
  ){
    return ResponseEntity.ok(expenseService.saveExpanse(expense));
  }

  @GetMapping("/all-expenses")
  public ResponseEntity<?> getAllResponses(
    @RequestParam(name = "page", defaultValue = "0", required = false) int page,
    @RequestParam(name = "size", defaultValue = "10", required = false) int size,
    Authentication connectedUser
  ) throws Exception{
    ApiResponse<PageResponse<ExpenseResponse>> res = expenseService.getAllExpenses(page,size,connectedUser);
    return ResponseEntity.status(res.getStatusCode()).body(res);
  }

}
