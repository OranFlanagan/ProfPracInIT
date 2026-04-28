package com.TicketingApp.Service;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.key}")
    private String SUPABASE_KEY;

    @Value("${supabase.bucket}")
    private String BUCKET;


    public String uploadFile(MultipartFile file, String ticketId) throws IOException {
        String fileName = ticketId + "_" + file.getOriginalFilename();
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SUPABASE_KEY);
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

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

    public String getPublicUrl(String fileName) {
        return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + fileName;
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
