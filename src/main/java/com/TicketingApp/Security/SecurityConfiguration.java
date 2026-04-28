package com.TicketingApp.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private SupabaseAuthenticationProvider supabaseAuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(supabaseAuthenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/email-form/**").permitAll()
                .requestMatchers("/invite/**").permitAll()
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/login/forgot-password").permitAll()
                .requestMatchers("/support-page/**").permitAll()
                .requestMatchers("/common-issues/**").permitAll()
                .requestMatchers("/styles/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/product-cards.html").permitAll()
                .requestMatchers("/autocomplete.html").permitAll()
                .requestMatchers("/suggestion-result.html").permitAll()
                .requestMatchers("/api/issues/*/fixes").permitAll() 
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/navigation-page", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(Customizer.withDefaults())
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
            );

        return http.build();
    }
}