package com.TicketingApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TicketingApp.Entity.Issue;
import java.util.*;
import org.springframework.stereotype.Repository;


@Repository
public interface IssuesRepository extends JpaRepository<Issue, Long>{

    List<Issue> findByProductId(int product_id);

}
