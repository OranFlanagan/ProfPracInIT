package com.TicketingApp.Repository;

import com.TicketingApp.Entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    List<TicketMessage> findByTicketOrderNumOrderByTimestampAsc(Long orderNum);
}
