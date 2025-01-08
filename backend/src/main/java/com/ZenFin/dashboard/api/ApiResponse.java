package com.ZenFin.dashboard.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
  private int statusCode;
  private String message;
  private T data;
}
