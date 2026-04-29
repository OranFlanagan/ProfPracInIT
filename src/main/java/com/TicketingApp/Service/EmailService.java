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
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Autowired
    private EmailTemplateService emailTemplateService;

    public void sendSimpleEmail(Ticket ticket) {
        try {
            String resolvedFrom = requireValidAddress(fromAddress, "sender (app.mail.from)");
            String recipient = requireValidAddress(ticket.getEmail(), "recipient");

            String subject = String.format("[%d] | %s | %s",
                ticket.getOrderNum(),
                ticket.getEmail(),
                ticket.getName());

            String template = emailTemplateService.getTicketSubmittedTemplate();
            String htmlBody = template
                .replace("${ticket.issueDescription}", HtmlUtils.htmlEscape(ticket.getIssueDescription()))
                .replace("${ticket.name}", HtmlUtils.htmlEscape(ticket.getName()))
                .replace("${ticket.email}", HtmlUtils.htmlEscape(ticket.getEmail()))
                .replace("${ticket.orderNum}", String.valueOf(ticket.getOrderNum()));

            Resend resend = new Resend(resendApiKey);
            SendEmailRequest request = SendEmailRequest.builder()
                    .from(resolvedFrom)
                    .to(recipient)
                    .subject(subject)
                    .html(htmlBody)
                    .build();

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

    public void sendNewTicketNotification(Ticket ticket, List<String> recipients) {
        if (recipients == null || recipients.isEmpty()) return;

        String resolvedFrom;
        try {
            resolvedFrom = requireValidAddress(fromAddress, "sender (app.mail.from)");
        } catch (AddressException e) {
            logger.error("No valid sender address for ticket notification: {}", e.getMessage());
            return;
        }

        String subject = String.format("New Ticket #%d submitted by %s", ticket.getOrderNum(), ticket.getName());
        String htmlBody = String.format(
            "<h2>New Support Ticket Received</h2>" +
            "<p><strong>Ticket #:</strong> %d</p>" +
            "<p><strong>Customer:</strong> %s</p>" +
            "<p><strong>Email:</strong> %s</p>" +
            "<p><strong>Phone:</strong> %s</p>" +
            "<p><strong>Organisation:</strong> %s</p>" +
            "<p><strong>Issue:</strong></p><p>%s</p>",
            ticket.getOrderNum(),
            HtmlUtils.htmlEscape(ticket.getName()),
            HtmlUtils.htmlEscape(ticket.getEmail()),
            ticket.getPhoneNum() != null ? HtmlUtils.htmlEscape(ticket.getPhoneNum()) : "—",
            ticket.getSchoolOrganisation() != null ? HtmlUtils.htmlEscape(ticket.getSchoolOrganisation()) : "—",
            HtmlUtils.htmlEscape(ticket.getIssueDescription())
        );

        Resend resend = new Resend(resendApiKey);
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .from(resolvedFrom)
                    .to(recipients.toArray(new String[0]))
                    .subject(subject)
                    .html(htmlBody)
                    .build();
            SendEmailResponse response = resend.emails().send(request);
            logger.info("Ticket notification sent to {} recipient(s), id: {}", recipients.size(), response.getId());
        } catch (Exception e) {
            logger.error("Failed to send ticket notification: {}", e.getMessage());
        }
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
