package com.TicketingApp.Service;

import org.springframework.stereotype.Service;

import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Repository.TicketRepository;

import java.util.List;



//Seperation of concern so logic to save ticket with repo goes here instead of controller
@Service
public class TicketService {

  private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void createTicket(Ticket ticket) throws Exception {
        if (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty()) {
            ticket.setAttachmentData(ticket.getAttachment().getBytes());
            ticket.setAttachmentFilename(ticket.getAttachment().getOriginalFilename());
            ticket.setAttachmentContentType(ticket.getAttachment().getContentType());
        }
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



