package com.ZenFin.dashboard;

import com.ZenFin.dashboard.expanse.CategoryResponse;
import com.ZenFin.dashboard.transaction.TransactionResponse;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpanseBreakdown {

  @NotNull
  private List<CategoryResponse> categories;
  @NotNull
  private List<MonthlyTrends> monthlyTrends;
  @NotNull
  private List<TransactionResponse> recentTransactions;

}
