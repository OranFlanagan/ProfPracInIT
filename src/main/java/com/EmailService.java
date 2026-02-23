package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
 @Autowired
    private JavaMailSender mailSender; 

    public void sendSimpleEmail(Ticket ticket) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ticketorc@gmail.com");
        message.setTo(ticket.getEmail());
        
        // Format subject as [OrderNum] | Email | Name
        String subject = String.format("[%d] | %s | %s", 
            ticket.getOrderNum(), 
            ticket.getEmail(), 
            ticket.getName());
        message.setSubject(subject);
        
        // Set body to issue description
        message.setText(ticket.getIssueDescription());
        mailSender.send(message);
        logger.info("✅ Email sent successfully to: {} with subject: [{}] | {} | {}", 
            ticket.getEmail(), 
            ticket.getOrderNum(), 
            ticket.getEmail(), 
            ticket.getName());   
    }
}
