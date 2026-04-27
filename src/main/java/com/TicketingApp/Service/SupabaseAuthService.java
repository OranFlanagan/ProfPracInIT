package com.TicketingApp.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;

@Service
public class SupabaseAuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String key;

    private final RestClient restClient = RestClient.create();

    public boolean setPassword(String accessToken, String newPassword) {
        try {
            restClient.put()
                .uri(supabaseUrl + "/auth/v1/user")
                .header("apikey", key)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("password", newPassword))
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String login(String email, String password) {
        try {
            Map<?, ?> response = restClient.post()
                .uri(supabaseUrl + "/auth/v1/token?grant_type=password")
                .header("apikey", key)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", email, "password", password))
                .retrieve()
                .body(Map.class);

            if (response == null || !response.containsKey("access_token")) {
                throw new RuntimeException("Invalid credentials");
            }
            return (String) response.get("access_token");
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}
