package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamSource;

@Service
public class EmailService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
 @Autowired
    private JavaMailSender mailSender; 

    public void sendSimpleEmail(Ticket ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // Set multipart to true with UTF-8 encoding
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("ticketorc@gmail.com");
            helper.setTo(ticket.getEmail());
            
            // Format subject as [OrderNum] | Email | Name
            String subject = String.format("[%d] | %s | %s", 
                ticket.getOrderNum(), 
                ticket.getEmail(), 
                ticket.getName());
            helper.setSubject(subject);
            
            // Set body to issue description
            helper.setText(ticket.getIssueDescription());
            
            // Add attachment if provided
            if (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty()) {
                MultipartFile attachment = ticket.getAttachment();
                try {
                    InputStreamSource source = new InputStreamSource() {
                        @Override
                        public java.io.InputStream getInputStream() throws java.io.IOException {
                            return attachment.getInputStream();
                        }
                    };
                    helper.addAttachment(
                        attachment.getOriginalFilename(),
                        source,
                        attachment.getContentType()
                    );
                    logger.info("📎 Attachment added: {} ({})", 
                        attachment.getOriginalFilename(), 
                        attachment.getSize() + " bytes");
                } catch (Exception attachmentError) {
                    logger.error("⚠️ Could not attach file: {}", attachmentError.getMessage());
                    // Continue sending email without attachment
                }
            }
            
            mailSender.send(message);
            logger.info("✅ Email sent successfully to: {} with subject: [{}] | {} | {}", 
                ticket.getEmail(), 
                ticket.getOrderNum(), 
                ticket.getEmail(), 
                ticket.getName());   
        } catch (MessagingException e) {
            logger.error("❌ Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Failed to process email: " + e.getMessage(), e);
        }
    }
}
