package com.ZenFin.dashboard.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
  private String type;
  private String category;
  private BigDecimal amount;
  private LocalDate date;
}
