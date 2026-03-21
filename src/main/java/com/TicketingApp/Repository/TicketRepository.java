package com.TicketingApp.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.TicketingApp.Entity.Ticket;


public interface TicketRepository extends JpaRepository<Ticket, Long>{

}
