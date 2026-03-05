package com;

import org.springframework.stereotype.Service;
import java.util.List;



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

    // Return all tickets for display on the staff dashboard
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Find a ticket by its ID
    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }
}



