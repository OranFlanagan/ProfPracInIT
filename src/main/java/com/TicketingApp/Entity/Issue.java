package com.TicketingApp.Entity;
import com.TicketingApp.Entity.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Issue {
    
    @Id
    private long Id;

    private String Title;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    
}
