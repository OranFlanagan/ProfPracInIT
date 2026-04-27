package com.TicketingApp.Service;


import com.TicketingApp.Entity.Issue;
import com.TicketingApp.Repository.IssuesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class IssuesService {
    

    @Autowired
    private IssuesRepository issuesRepository;

    public List<Issue> getIssuesByProduct(Long product_id) {
        return issuesRepository.findByProduct_Id(product_id);
    }

}
