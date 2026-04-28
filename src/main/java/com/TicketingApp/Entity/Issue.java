package com.TicketingApp.Entity;
import com.TicketingApp.Entity.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Issue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    private String Title;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    
}
