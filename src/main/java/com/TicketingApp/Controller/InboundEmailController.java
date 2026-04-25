  package com.TicketingApp.Controller;

import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Entity.EmailMessage;
import com.TicketingApp.Repository.EmailMessageRepository;
import com.TicketingApp.Repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/email")
public class InboundEmailController {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EmailMessageRepository emailMessageRepository;

    private static final Logger logger = LoggerFactory.getLogger(InboundEmailController.class);


    /**
     * This endpoint is set as the webhook target in your email provider.
     * It expects a generic payload (e.g., Mailgun/SendGrid) and extracts ticketId from the subject line.
     * Example subject: "[11] | ..."
     */
    @PostMapping("/inbound")
    public ResponseEntity<String> receiveInboundEmail(
            @RequestHeader(value = "X-Webhook-Token", required = false) String token,
            @RequestBody GenericEmailPayload payload) {
        final String SECRET_TOKEN = "YOUR_SECRET_TOKEN"; // Change this to a secure value and keep it secret!
        if (token == null || !SECRET_TOKEN.equals(token)) {
            System.out.println("[INBOUND EMAIL] Invalid or missing webhook token");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or missing webhook token");
        }

        System.out.println("[INBOUND EMAIL] Received: subject='" + payload.getSubject() + "', from='" + payload.getFrom() + "', body='" + payload.getBody() + "'");
        logger.info("Received inbound email webhook: subject={}, from={}, body={}", payload.getSubject(), payload.getFrom(), payload.getBody());

        // Extract ticketId from subject, e.g., [11] | ...
        Long ticketId = extractTicketId(payload.getSubject());
        if (ticketId == null) {
            logger.warn("No ticketId found in subject: {}", payload.getSubject());
            System.out.println("[INBOUND EMAIL] No ticketId found in subject: '" + payload.getSubject() + "'");
            return ResponseEntity.badRequest().body("No ticketId found in subject");
        }

        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            logger.warn("Ticket not found for id: {}", ticketId);
            System.out.println("[INBOUND EMAIL] Ticket not found for id: " + ticketId);
            return ResponseEntity.badRequest().body("Ticket not found");
        }

        EmailMessage email = new EmailMessage();
        email.setTicket(ticketOpt.get());
        email.setSender(payload.getFrom());
        email.setRecipient(null); // Set if you have recipient info
        email.setSubject(payload.getSubject());
        email.setBody(payload.getBody());
        email.setTimestamp(LocalDateTime.now());
        email.setInbound(true); // Mark as inbound
        emailMessageRepository.save(email);
        System.out.println("[INBOUND EMAIL] Email message saved for ticket " + ticketId);
        logger.info("Email message saved for ticket {}", ticketId);
        return ResponseEntity.ok("Message saved");
    }

    /**
     * Extracts ticketId from subject line in the format [11] | ...
     */
    private Long extractTicketId(String subject) {
        if (subject == null) return null;
        // Match a number inside the first pair of square brackets
        Pattern pattern = Pattern.compile("^\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(subject.trim());
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                logger.error("Failed to parse ticketId from subject: {}", subject);
            }
        }
        return null;
    }

    // Generic DTO for inbound email webhook (matches common provider payloads)
    public static class GenericEmailPayload {
        private String subject;
        private String from;
        private String body;

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}
