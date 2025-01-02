package com.ZenFin.dashboard.exapanse;

import com.ZenFin.user.User;
import jakarta.persistence.*;
import lombok.*;
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
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID expenseId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private BigDecimal amount;
  private String category;
  private LocalDate date;
  private boolean recurring;




}
