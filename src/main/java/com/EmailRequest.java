package com;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class EmailRequest {
    @NotBlank(message = "Recipient is required")
    @Email(message = "Invalid email format")
    private String to;

    @NotBlank(message = "Subject is required")
    private String subject;

    private String body;

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
