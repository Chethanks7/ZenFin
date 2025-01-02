package com.ZenFin.dashboard;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;




}
