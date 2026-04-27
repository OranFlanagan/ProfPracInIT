package com.TicketingApp.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupabaseAdminService {

    @Value("${supabase.service_role_key}")
    private String serviceRoleKey;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${app.url}")
    private String appUrl;

    private final RestClient restClient = RestClient.create();

    private HttpHeaders adminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", serviceRoleKey);
        headers.setBearerAuth(serviceRoleKey);
        return headers;
    }

    // Returns null on success, or an error message string on failure
    public String inviteUser(String email) {
        String url = supabaseUrl + "/auth/v1/invite";
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("redirect_to", appUrl + "/invite/accept");
        try {
            restClient.post()
                .uri(url)
                .headers(h -> h.addAll(adminHeaders()))
                .body(body)
                .retrieve()
                .toBodilessEntity();
            return null;
        } catch (HttpClientErrorException e) {
            return switch (e.getStatusCode().value()) {
                case 422 -> "'" + email + "' is already registered or has a pending invite.";
                case 429 -> "Too many invites sent recently. Please wait a few minutes and try again.";
                case 400 -> "'" + email + "' is not a valid email address.";
                case 401, 403 -> "Server configuration error: invalid API key. Contact your administrator.";
                default -> "Supabase returned an unexpected error (" + e.getStatusCode().value() + "). Please try again.";
            };
        } catch (Exception e) {
            return "Could not reach the authentication server. Check your internet connection and try again.";
        }
    }

    // Returns true if deleted or user did not exist in Supabase; false on error
    public boolean deleteUser(String email) {
        try {
            Map<String, Object> listBody = restClient.get()
                .uri(supabaseUrl + "/auth/v1/admin/users?filter=" + email)
                .headers(h -> h.addAll(adminHeaders()))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (listBody == null) return false;

            List<?> users = (List<?>) listBody.get("users");
            if (users == null || users.isEmpty()) return true;

            String supabaseUserId = null;
            for (Object userObj : users) {
                Map<?, ?> user = (Map<?, ?>) userObj;
                if (email.equals(user.get("email"))) {
                    supabaseUserId = user.get("id").toString();
                    break;
                }
            }
            if (supabaseUserId == null) return true;

            restClient.delete()
                .uri(supabaseUrl + "/auth/v1/admin/users/" + supabaseUserId)
                .headers(h -> h.addAll(adminHeaders()))
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value() == 404;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendPasswordReset(String email) {
        String url = supabaseUrl + "/auth/v1/recover";
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("redirect_to", appUrl + "/login");
        try {
            restClient.post()
                .uri(url)
                .headers(h -> h.addAll(adminHeaders()))
                .body(body)
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
