package com.TicketingApp.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_variable") 
public class UserVariable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean receiveNotifications = false;

   public static enum Role {

    ROLE_STAFF,
    ROLE_ADMIN;
}


    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isReceiveNotifications() { return receiveNotifications; }
    public void setReceiveNotifications(boolean receiveNotifications) { this.receiveNotifications = receiveNotifications; }
}
