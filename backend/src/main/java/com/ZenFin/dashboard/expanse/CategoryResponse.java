package com.ZenFin.dashboard.expanse;

import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

  private String name ;
  private float amount ;
}
