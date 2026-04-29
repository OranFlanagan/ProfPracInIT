package com.TicketingApp.Controller;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TicketingApp.Entity.DeflectionSuccess;
import com.TicketingApp.Repository.DeflectionSuccessRepository;

@RestController
public class DeflectionSuccessController {

    @Autowired
    private DeflectionSuccessRepository deflectionSuccessRepository;

    @PostMapping("/common-issues/deflection-success")
    public ResponseEntity<?> recordSuccess(@RequestBody Map<String, Long> body) {
            System.out.println(">>> HIT deflection endpoint: " + body);

        DeflectionSuccess record = new DeflectionSuccess();
        record.setIssueId(body.get("issueId"));
        record.setProductId(body.get("productId"));
        record.setResolvedAt(LocalDateTime.now());
        deflectionSuccessRepository.save(record);
            System.out.println(">>> Saved record id: " + record.getId());

        return ResponseEntity.ok().build();
    }
}




