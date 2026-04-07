package com.TicketingApp.Entity;
import com.TicketingApp.Entity.Issue;

import java.util.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class Product {
    
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    
    private String name;

    private String catagory;

    private String urlString;


    // one to many relationship rule needed for list as database cannot store a list and needs to use forgein keys to know what common issue a product migh have by referencing 
    //another table
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

}
