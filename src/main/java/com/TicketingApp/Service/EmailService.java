package com.TicketingApp.Service;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.mail.from:no-reply@conordurcan.site}")
    private String fromAddress;

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Autowired
    private EmailTemplateService emailTemplateService;

    public void sendSimpleEmail(Ticket ticket) {
        try {
            // Validate and resolve sender and recipient addresses
            String resolvedFrom = resolveValidAddress(fromAddress, "no-reply@conordurcan.site");
            String recipient = requireValidAddress(ticket.getEmail(), "recipient");

            // Prepare email subject and HTML body
            String subject = String.format("[%d] | %s | %s",
                ticket.getOrderNum(),
                ticket.getEmail(),
                ticket.getName());

            // Get template and replace variables
            String template = emailTemplateService.getTicketSubmittedTemplate();
            String htmlBody = template
                .replace("${ticket.issueDescription}", HtmlUtils.htmlEscape(ticket.getIssueDescription()))
                .replace("${ticket.name}", HtmlUtils.htmlEscape(ticket.getName()))
                .replace("${ticket.email}", HtmlUtils.htmlEscape(ticket.getEmail()))
                .replace("${ticket.orderNum}", String.valueOf(ticket.getOrderNum()));

            // Initialize Resend client with API key
            Resend resend = new Resend(resendApiKey);

            // Build the Resend email request
            SendEmailRequest request = SendEmailRequest.builder()
                    .from(resolvedFrom)
                    .to(recipient)
                    .subject(subject)
                    .html(htmlBody)
                    .build();

            // Send the email and log the response
            SendEmailResponse response = resend.emails().send(request);
            logger.info("Email sent via Resend, id: {}", response.getId());

        } catch (AddressException e) {
            logger.error("Invalid email address: {}", e.getMessage());
            throw new RuntimeException("Invalid email address: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to send email via Resend: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String resolveValidAddress(String candidate, String fallback) throws AddressException {
        if (isValidAddress(candidate)) return candidate.trim();
        if (isValidAddress(fallback)) {
            logger.warn("Invalid app.mail.from '{}'; falling back to {}", candidate, fallback);
            return fallback;
        }
        throw new AddressException("No valid sender email address configured");
    }

    private String requireValidAddress(String candidate, String label) throws AddressException {
        if (!isValidAddress(candidate)) {
            throw new AddressException("Invalid " + label + " email address: " + candidate);
        }
        return candidate.trim();
    }

    private boolean isValidAddress(String candidate) {
        if (candidate == null || candidate.trim().isEmpty()) return false;
        try {
            new InternetAddress(candidate.trim(), true).validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
}