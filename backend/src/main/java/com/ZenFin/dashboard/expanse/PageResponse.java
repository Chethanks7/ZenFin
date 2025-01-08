package com.ZenFin.dashboard.expanse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

  private HttpStatus status;
  @NotNull
  private List<T> contents;
  private int page;
  private int size;
  private int totalPages;
  private long totalElements;
  private boolean hasNext;
  private boolean hasPrevious;
}
