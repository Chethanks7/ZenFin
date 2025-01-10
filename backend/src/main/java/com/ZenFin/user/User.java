package com.ZenFin.user;

import com.ZenFin.dashboard.budget.Budget;
import com.ZenFin.dashboard.expanse.Expense;
import com.ZenFin.dashboard.income.Income;
import com.ZenFin.dashboard.transaction.Transaction;
import com.ZenFin.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable, UserDetails , Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Role> roles;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private boolean accountLocked;

    private boolean enabled;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    private String profilePictureUrl;

    private boolean twoFactorEnabled;

    private boolean isEmailVerified;

    private LocalDateTime lastLogin;

    private int failedLoginAttempts;

    private String securityQuestion;

    private String securityAnswer;

    private LocalDateTime lastEmailSentTime;

    private byte resendAttempts;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Transaction> transactions;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Income> incomes;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Expense> expenses;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Budget> budgets;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked && failedLoginAttempts < 5; // Example logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement business logic
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    @Override
    public String getName() {
        return email;
    }
}
