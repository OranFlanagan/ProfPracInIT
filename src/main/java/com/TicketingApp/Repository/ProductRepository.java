package com.TicketingApp.Repository;
import com.TicketingApp.Entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.List;


@Repository
public interface ProductRepository  extends JpaRepository <Product, Long>
{
//probably will need some sort of mapped by logic in here
List<Product> findByFeaturedOnSupportPageTrue();
List<Product> findByFeaturedOnSupportPageTrueAndNameContainingIgnoreCase(String name);


// search assist
List<Product> findByFeaturedOnSupportPageTrueAndNameStartingWithIgnoreCase(String prefix);
}
