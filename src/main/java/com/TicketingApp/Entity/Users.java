package com.TicketingApp.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
Long id;

public static enum Roles {

    ROLE_STAFF,
    ROLE_ADMIN;
}
@Enumerated(EnumType.STRING)
private Roles role;

private String username;

private String password;


}
