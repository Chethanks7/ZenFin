package com.ZenFin.dashboard.pdfService;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfExpanseData {
  private String date ;
  private String category;
  private String description;
  private BigDecimal amount ;
}
