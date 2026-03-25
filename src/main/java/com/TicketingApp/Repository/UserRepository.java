package com.TicketingApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.TicketingApp.Entity.Users;


public interface UserRepository extends JpaRepository<Users, Long>{
    Users findByUsername(String username);

}
