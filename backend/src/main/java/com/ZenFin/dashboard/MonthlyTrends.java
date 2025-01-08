package com.ZenFin.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrends {
  @NonNull
  private List<BigDecimal> incomes;
  @NonNull
  private List<BigDecimal> expenses;
  @NonNull
  private List<String> months;
}
