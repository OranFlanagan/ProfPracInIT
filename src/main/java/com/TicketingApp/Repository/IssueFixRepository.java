package com.TicketingApp.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import org.springframework.stereotype.Repository;
import com.TicketingApp.Entity.*;

@Repository
public interface IssueFixRepository extends JpaRepository<IssueFix, Long> {

    List<IssueFix>findByIssue_id(Long id);

}
