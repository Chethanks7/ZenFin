package com.ZenFin.dashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewResponse {

  private String status;
  private float totalIncome;
  private float totalExpense;
  private float totalBudget;

  private ExpanseBreakdown expanseBreakdown;


}
