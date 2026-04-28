package com.TicketingApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TicketingApp.Entity.EmailMessage;
import com.TicketingApp.Entity.Ticket;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    void deleteByTicket(Ticket ticket);
}
