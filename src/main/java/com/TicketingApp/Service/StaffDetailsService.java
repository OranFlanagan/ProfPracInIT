package com.TicketingApp.Service;

import org.springframework.stereotype.Service;
import com.TicketingApp.Entity.Users;
import com.TicketingApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//some sort of interface needed in spring security for some mad methods
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
public class StaffDetailsService implements UserDetailsService  {
    
    @Autowired
    private UserRepository userRepository;

     @Override
    public UserDetails loadUserByUsername(String username) 
    throws UsernameNotFoundException {

        Users user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );


}
}
