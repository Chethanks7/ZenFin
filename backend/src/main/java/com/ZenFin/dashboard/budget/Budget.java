package com.ZenFin.dashboard.budget;


import com.ZenFin.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID budgetId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private BigDecimal amount;  // Set budget amount
  private int month;          // 1 to 12
  private int year;
  @CreatedDate
  private LocalDate createdAt;
}
