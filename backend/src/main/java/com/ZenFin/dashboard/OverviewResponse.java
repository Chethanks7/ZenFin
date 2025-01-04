package com.ZenFin.dashboard;

import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverviewResponse {

  private String status;
  private float totalIncome;
  private float totalExpense;
  private float totalBudget;

  private ExpanseBreakdown expanseBreakdown;


}
