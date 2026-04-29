package com.TicketingApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TicketingApp.Entity.UserVariable;
import java.util.List;

public interface UserVariableRepository extends JpaRepository<UserVariable, Long> {
    UserVariable findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserVariable> findByReceiveNotificationsTrue();
}
