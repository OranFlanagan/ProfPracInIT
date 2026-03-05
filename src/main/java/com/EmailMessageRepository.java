package com;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    List<EmailMessage> findByTicketOrderByTimestampAsc(Ticket ticket);
}
