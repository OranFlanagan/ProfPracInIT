package com.TicketingApp.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/email-form/**").permitAll()
            .requestMatchers("/support-page/**").permitAll()
            .requestMatchers("/common-issues/**").permitAll()
            .requestMatchers("/common-issues.css/**").permitAll()
            .requestMatchers("/product-log.css/**").permitAll()
            .requestMatchers("/styles/**", "/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        ).formLogin(Customizer.withDefaults());
        return http.build();
    }


}
