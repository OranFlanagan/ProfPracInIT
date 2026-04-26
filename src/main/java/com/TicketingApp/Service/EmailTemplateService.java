package com.TicketingApp.Service;

import com.TicketingApp.Entity.EmailTemplate;
import com.TicketingApp.Repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EmailTemplateService {
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    public String getTicketSubmittedTemplate() {
        Optional<EmailTemplate> template = emailTemplateRepository.findByName("ticket_submitted");
        return template.map(EmailTemplate::getContent).orElse("<p>Ticket submitted: ${ticket.issueDescription}</p>");
    }

    public void updateTicketSubmittedTemplate(String content) {
        EmailTemplate template = emailTemplateRepository.findByName("ticket_submitted")
                .orElse(new EmailTemplate("ticket_submitted", content));
        template.setContent(content);
        emailTemplateRepository.save(template);
    }
}
