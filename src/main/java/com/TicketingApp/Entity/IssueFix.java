package com.TicketingApp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
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
public class IssueFix {
    
@Id
public long Id;


public String fixDescription;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

}
