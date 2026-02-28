package com;

import org.springframework.stereotype.Service;



//Seperation of concern so logic to save ticket with repo goes here instead of controller
@Service
public class TicketService {

  private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void createTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }
}



