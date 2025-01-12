package com.ZenFin.dashboard.expanse;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyExpenseSummaryResponse {

  private String userId;
  List<MonthlyExpenseSummary> summaries;

}
