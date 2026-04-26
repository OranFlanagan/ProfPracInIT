package com.TicketingApp.Repository;

import com.TicketingApp.Entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    Optional<EmailTemplate> findByName(String name);
}
