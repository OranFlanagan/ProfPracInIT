package com.TicketingApp.Repository;
import com.TicketingApp.Entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByFeaturedOnSupportPageTrue();

    List<Product> findByFeaturedOnSupportPageTrueAndNameContainingIgnoreCase(String name);

    List<Product> findByFeaturedOnSupportPageTrueAndNameStartingWithIgnoreCase(String prefix);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.issues")
    List<Product> findAllWithIssues();


    // new — for admin (all products, no featured filter)
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByNameStartingWithIgnoreCase(String prefix);
}