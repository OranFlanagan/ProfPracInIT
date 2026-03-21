package com.TicketingApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TicketingApp.Entity.EmailMessage;
import com.TicketingApp.Entity.Ticket;

import java.util.List;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    List<EmailMessage> findByTicketOrderByTimestampAsc(Ticket ticket);
}
