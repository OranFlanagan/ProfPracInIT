package com.TicketingApp.Security;

import com.TicketingApp.Service.SupabaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import com.TicketingApp.Repository.UserVariableRepository;
import com.TicketingApp.Entity.UserVariable;



import java.util.List;

@Component
public class SupabaseAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @Autowired
    private UserVariableRepository userVariableRepository;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        String token;
        try {
            token = supabaseAuthService.login(email, password);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        UserVariable user = userVariableRepository.findByEmail(email);
        if (user == null) {
            throw new BadCredentialsException("Account not provisioned. Contact an administrator.");
        }
        if (user.getRole() == null) {
            throw new BadCredentialsException("Account has no role assigned. Contact an administrator.");
        }
        return new UsernamePasswordAuthenticationToken(
            email, token, List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}