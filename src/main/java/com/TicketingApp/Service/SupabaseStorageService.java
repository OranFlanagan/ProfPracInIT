package com.TicketingApp.Service;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.service_role_key}")
    private String SUPABASE_KEY;

    @Value("${supabase.bucket}")
    private String BUCKET;

   



  public String uploadFile(MultipartFile file, String ticketId) throws IOException {
    String fileName = ticketId + "_" + file.getOriginalFilename().replace(" ", "_");
    String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + fileName;

    String contentType = file.getContentType();
    if (contentType == null || contentType.isBlank()) {
        contentType = "application/octet-stream";
    }

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(SUPABASE_KEY);
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.set("x-upsert", "true");

    HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

    try {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return fileName;
        } else {
            throw new RuntimeException("Failed to upload file to Supabase: " + response.getBody());
        }
    } catch (RuntimeException e) {
        throw e;
    } catch (Exception e) {
        throw new IOException("Upload failed: " + e.getMessage(), e);
    }
}
    public String getSignedUrl(String fileName) {
        String url = SUPABASE_URL + "/storage/v1/object/sign/" + BUCKET + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SUPABASE_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>("{\"expiresIn\": 3600}", headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, new org.springframework.core.ParameterizedTypeReference<>() {});
        String signedPath = (String) response.getBody().get("signedURL");
        return SUPABASE_URL + "/storage/v1" + signedPath;
    }

    public boolean deleteFile(String fileName) {
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SUPABASE_KEY);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
