package com.ZenFin.dashboard.expanse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

}
