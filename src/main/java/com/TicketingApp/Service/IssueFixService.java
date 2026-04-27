package com.TicketingApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TicketingApp.Entity.Issue;
import com.TicketingApp.Entity.IssueFix;
import com.TicketingApp.Repository.IssueFixRepository;
import com.TicketingApp.Repository.IssuesRepository;
import java.util.*;

@Service
public class IssueFixService {
    
@Autowired
private IssueFixRepository issueFixRepository;

 public List<IssueFix> getIssuesFixByIssues(Long issues_id) {
        return issueFixRepository.findByIssue_id(issues_id);
    }

}


