package com.TicketingApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TicketingApp.Entity.UserVariable;

public interface UserVariableRepository extends JpaRepository<UserVariable, Long> {
    UserVariable findByEmail(String email);
    boolean existsByEmail(String email);
}
