package com.ZenFin.dashboard.expanse;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpanseRepository extends JpaRepository<Expense,UUID> {
}
