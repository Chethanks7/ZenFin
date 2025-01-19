package com.ZenFin.dashboard.expanse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyExpanseTrend {
  private String day;
  private double totalSpent ;

}
