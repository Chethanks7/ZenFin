package com.ZenFin.dashboard.expanse;

import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopThreeExpenseDTO {

  private String category ;
  private BigDecimal totalSpent;
  private Double percentage;

}
