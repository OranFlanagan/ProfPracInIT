package com.TicketingApp.Controller;

import com.TicketingApp.Entity.IssueFix;
import com.TicketingApp.Service.IssueFixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueFixController {

    @Autowired
    private IssueFixService issueFixService;

    @GetMapping("/{issueId}/fixes")
    public List<IssueFix> getFixes(@PathVariable Long issueId) {
        return issueFixService.getIssuesFixByIssues(issueId);
    }
}