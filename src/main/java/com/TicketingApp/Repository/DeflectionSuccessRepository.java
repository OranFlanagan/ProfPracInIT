package com.TicketingApp.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TicketingApp.Entity.*;



@Repository
public interface DeflectionSuccessRepository extends JpaRepository<DeflectionSuccess, Long> {
    long countByIssueId(Long issueId);
    long countByProductId(Long productId);
}