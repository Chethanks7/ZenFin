package com.ZenFin.dashboard.income;

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
public class Income {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID incomeId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private BigDecimal amount;
  private String source;  // E.g., "Salary", "Freelance"
  private LocalDate date;
  private boolean recurring;

}
