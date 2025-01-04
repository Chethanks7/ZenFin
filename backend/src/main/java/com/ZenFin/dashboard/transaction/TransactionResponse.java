package com.ZenFin.dashboard.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
  private String type;
  private String category;
  private BigDecimal amount;
  private LocalDate date;
}
