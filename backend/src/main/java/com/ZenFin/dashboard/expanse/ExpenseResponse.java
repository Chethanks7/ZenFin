package com.ZenFin.dashboard.expanse;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

  @NotNull
  private String id;
  @NotNull
  private BigDecimal amount;
  @NotNull
  private String category;
  @NotNull
  private String date;
  @NotNull
  private String userId;
}
