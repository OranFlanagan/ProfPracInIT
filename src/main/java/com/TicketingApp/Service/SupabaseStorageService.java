package com.TicketingApp.Service;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SupabaseStorageService {
        /**
         * Removes an attachment from the Supabase bucket by filename.
         * @param fileName The name of the file to remove (e.g., ticket-..._filename.ext)
         * @return true if deleted successfully, false otherwise
         */
        public boolean deleteFile(String fileName) {
            String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + fileName;
            System.out.println("[SupabaseStorageService] Deleting file: " + fileName);
            System.out.println("[SupabaseStorageService] DELETE URL: " + url);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(SUPABASE_KEY);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
                System.out.println("[SupabaseStorageService] Delete response status: " + response.getStatusCode());
                return response.getStatusCode().is2xxSuccessful();
            } catch (Exception e) {
                System.out.println("[SupabaseStorageService] Exception during delete: " + e.getMessage());
                return false;
            }
        }
    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.key}")
    private String SUPABASE_KEY;

    @Value("${supabase.bucket}")
    private String BUCKET;


    public String uploadFile(MultipartFile file, String ticketId) throws IOException {
        // Debug: Print the first 8 characters of each config value (never print full key)
        System.out.println("[SupabaseStorageService] SUPABASE_URL: " + (SUPABASE_URL != null ? SUPABASE_URL.substring(0, Math.min(8, SUPABASE_URL.length())) : "null"));
        System.out.println("[SupabaseStorageService] SUPABASE_KEY: " + (SUPABASE_KEY != null ? SUPABASE_KEY.substring(0, Math.min(8, SUPABASE_KEY.length())) : "null"));
        System.out.println("[SupabaseStorageService] BUCKET: " + (BUCKET != null ? BUCKET.substring(0, Math.min(8, BUCKET.length())) : "null"));
        String fileName = ticketId + "_" + file.getOriginalFilename();
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + fileName;

        System.out.println("[SupabaseStorageService] Uploading file: " + fileName);
        System.out.println("[SupabaseStorageService] Target URL: " + url);
        System.out.println("[SupabaseStorageService] Content-Type: " + file.getContentType());
        System.out.println("[SupabaseStorageService] File size: " + file.getSize());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(SUPABASE_KEY);
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            System.out.println("[SupabaseStorageService] Response status: " + response.getStatusCode());
            System.out.println("[SupabaseStorageService] Response body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return fileName;
            } else {
                throw new RuntimeException("Failed to upload file to Supabase: " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("[SupabaseStorageService] Exception during upload: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String getPublicUrl(String fileName) {
        return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + fileName;
    }
}
