package com;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import org.springframework.web.multipart.MultipartFile;

//H2 dependencies 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;


@Entity
public class Ticket {

    @Id
    //ensures that id auto increments so no ticket is the same
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderNum;

    @NotBlank(message = "Name is required")
    private String Name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNum;

    private String issueDescription;

    @Transient
    private MultipartFile attachment;

    public int getOrderNum() { return orderNum; }
    public void setOrderNum(int orderNum) { this.orderNum = orderNum; }

    public String getName() { return Name; }
    public void setName(String name) { this.Name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public MultipartFile getAttachment() { return attachment; }
    public void setAttachment(MultipartFile attachment) { this.attachment = attachment; }
}
