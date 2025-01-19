package com.ZenFin.dashboard.expanse;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyTrends {
  private String userId;
  private String startDate;
  private String endDate;
  private List<WeeklyExpanseTrend> weeklyExpanseTrends;
}
