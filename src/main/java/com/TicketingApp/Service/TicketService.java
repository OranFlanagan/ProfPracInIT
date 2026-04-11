package com.TicketingApp.Service;

import org.springframework.stereotype.Service;

import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Entity.TicketStatus;
import com.TicketingApp.Repository.TicketRepository;

import java.util.List;
import java.util.Set;



//Seperation of concern so logic to save ticket with repo goes here instead of controller
@Service
public class TicketService {

  private final TicketRepository ticketRepository;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "png", "jpg", "jpeg", "gif", "doc", "docx", "txt", "csv", "xlsx"
    );

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void createTicket(Ticket ticket) throws Exception {
        if (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty()) {
            validateFileType(ticket.getAttachment().getOriginalFilename());
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

    private void validateFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException(
                    "File must have an extension. Accepted: " + ALLOWED_EXTENSIONS);
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "File type '" + extension + "' is not allowed. Accepted: " + ALLOWED_EXTENSIONS);
        }
    }

    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket ticket = findById(id);
        if (ticket == null) {
            return null;
        }

        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }
}



