package com.ZenFin.dashboard.expanse;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class MonthlyExpenseSummary {
  private String category;
  private BigDecimal totalAmount;
  private Integer month;
  private Integer year;

  public MonthlyExpenseSummary(String category, BigDecimal totalAmount, Integer month, Integer year) {
    this.category = category;
    this.totalAmount = totalAmount;
    this.month = month;
    this.year = year;
  }

}

