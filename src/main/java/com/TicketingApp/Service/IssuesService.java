package com.TicketingApp.Service;


import com.TicketingApp.Repository.UserRepository;
import com.TicketingApp.Entity.Issue;
import com.TicketingApp.Repository.IssuesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class IssuesService {
    

    @Autowired
    private IssuesRepository issuesRepository;

    public List<Issue> getIssuesByProduct(int product_id) {
        return issuesRepository.findByProductId(product_id);
    }

}
