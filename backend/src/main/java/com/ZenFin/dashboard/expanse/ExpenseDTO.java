package com.ZenFin.dashboard.expanse;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

  @Valid
  @NotNull(message = "Amount cannot be empty")
  private BigDecimal amount;

  @NotNull(message = "Description cannot be empty")
  private String description;

  @NotNull(message = "Category cannot be empty")
  private String category;

  @Valid
  @NotNull(message = "Date format is not matched")
  private LocalDate date;
}
