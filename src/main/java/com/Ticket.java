package com;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class Ticket {
    private int orderNum;

    @NotBlank(message = "Name is required")
    private String Name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNum;

    private String issueDescription;

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
}
