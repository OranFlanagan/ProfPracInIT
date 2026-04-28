package com.TicketingApp.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
//H2 dependencies 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
@Entity
public class Ticket {

    

    @Id
    //ensures that id auto increments so no ticket is the same
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderNum;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNum;

    @Column(name = "invoice_num")
    private String invoiceNum;

    @Column(name = "school_organisation")
    private String schoolOrganisation;

    @Column(columnDefinition = "TEXT")
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.UNASSIGNED;

    @Transient
    private MultipartFile attachment;



    private String attachmentFilename;

    private String attachmentUrl;

    private String supabaseFilename;

    @Column(name = "creation_time", nullable = false, updatable = false)
    private LocalDateTime creationTime;

    private String assignedStaff;

    @Column(columnDefinition = "TEXT")
    private String internalNotes;

    public Long getOrderNum() { return orderNum; }
    public void setOrderNum(Long orderNum) { this.orderNum = orderNum; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

        public String getInvoiceNum() { return invoiceNum; }
        public void setInvoiceNum(String invoiceNum) { this.invoiceNum = invoiceNum; }

        public String getSchoolOrganisation() { return schoolOrganisation; }
        public void setSchoolOrganisation(String schoolOrganisation) { this.schoolOrganisation = schoolOrganisation; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public MultipartFile getAttachment() { return attachment; }
    public void setAttachment(MultipartFile attachment) { this.attachment = attachment; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }




    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

    public String getSupabaseFilename() { return supabaseFilename; }
    public void setSupabaseFilename(String supabaseFilename) { this.supabaseFilename = supabaseFilename; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }



    public LocalDateTime getCreationTime() { return creationTime; }
    public void setCreationTime(LocalDateTime creationTime) { this.creationTime = creationTime; }

    public String getAssignedStaff() { return assignedStaff; }
    public void setAssignedStaff(String assignedStaff) { this.assignedStaff = assignedStaff; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.creationTime = LocalDateTime.now();
    }
}
