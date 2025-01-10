package com.ZenFin.dashboard.api;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
  private HttpStatus statusCode;
  private String message;
  private T data;
}
