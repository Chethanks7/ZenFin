package com.ZenFin.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrends {
  @NonNull
  private List<BigDecimal> incomes;
  @NonNull
  private List<BigDecimal> expenses;
  @NonNull
  private List<String> months;
}
