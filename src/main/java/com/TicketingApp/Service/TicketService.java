package com.TicketingApp.Service;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.springframework.stereotype.Service;

import com.TicketingApp.Entity.Ticket;
import com.TicketingApp.Entity.TicketStatus;
import com.TicketingApp.Repository.EmailMessageRepository;
import com.TicketingApp.Repository.TicketRepository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Collections;


@Service
public class TicketService {

  private final TicketRepository ticketRepository;
    private final EmailMessageRepository emailMessageRepository;
    private static final Detector DETECTOR = new DefaultDetector();

    private static final Map<String, Set<String>> ALLOWED_FILE_TYPES = Map.ofEntries(
        Map.entry("pdf", Set.of("application/pdf")),
        Map.entry("png", Set.of("image/png")),
        Map.entry("jpg", Set.of("image/jpeg")),
        Map.entry("jpeg", Set.of("image/jpeg")),
        Map.entry("gif", Set.of("image/gif")),
        Map.entry("webp", Set.of("image/webp")),
        Map.entry("mp4", Set.of("video/mp4")),
        Map.entry("txt", Set.of("text/plain")),
        Map.entry("csv", Set.of("text/csv", "application/csv")),
        Map.entry("doc", Set.of("application/msword")),
        Map.entry("docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
        Map.entry("xlsx", Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        Map.entry("odt", Set.of(
                "application/vnd.oasis.opendocument.text",
                "application/x-vnd.oasis.opendocument.text"
        )),
        Map.entry("ods", Set.of(
                "application/vnd.oasis.opendocument.spreadsheet",
                "application/x-vnd.oasis.opendocument.spreadsheet"
        ))
    );

    private final SupabaseStorageService supabaseStorageService;

    public TicketService(TicketRepository ticketRepository, EmailMessageRepository emailMessageRepository, SupabaseStorageService supabaseStorageService) {
        this.ticketRepository = ticketRepository;
        this.emailMessageRepository = emailMessageRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    public void createTicket(Ticket ticket) throws Exception {
        if (ticket.getAttachment() != null && !ticket.getAttachment().isEmpty()) {
            String rawFilename = ticket.getAttachment().getOriginalFilename();
            String originalFilename = Paths.get(rawFilename != null ? rawFilename : "").getFileName().toString();
            // Validate file type to prevent spoofing
            validateAndDetectMimeType(ticket.getAttachment().getBytes(), originalFilename);
            String supabaseFileName = supabaseStorageService.uploadFile(ticket.getAttachment(), "ticket-" + System.currentTimeMillis());
            ticket.setAttachmentFilename(originalFilename); // for display/download
            ticket.setSupabaseFilename(supabaseFileName);   // for deletion
        }
        ticketRepository.save(ticket);
    }

    // Return all tickets for display on the staff dashboard
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "orderNum"));
    }

    /**
     * Returns tickets sorted by newest first. If oldestFirst is true, reverses the list in memory.
     */
    public List<Ticket> getAllTicketsSorted(boolean oldestFirst) {
        List<Ticket> tickets = ticketRepository.findAll();
        tickets.sort((t1, t2) -> t2.getCreationTime().compareTo(t1.getCreationTime())); // newest first
        if (oldestFirst) {
            Collections.reverse(tickets);
        }
        return tickets;
    }

    // Find a ticket by its ID
    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    String validateAndDetectMimeType(byte[] attachmentBytes, String filename) throws IOException {
        String extension = extractExtension(filename);
        Set<String> allowedMimeTypes = ALLOWED_FILE_TYPES.get(extension);

        if (allowedMimeTypes == null) {
            throw new IllegalArgumentException(
                    "File type '" + extension + "' is not allowed. Accepted: " + ALLOWED_FILE_TYPES.keySet());
        }

        String detectedMimeType = detectMimeType(attachmentBytes, filename);
        if (!allowedMimeTypes.contains(detectedMimeType)) {
            throw new IllegalArgumentException(
                    "File extension '" + extension + "' does not match detected MIME type '"
                            + detectedMimeType + "'. Expected one of: " + allowedMimeTypes);
        }

        return detectedMimeType;
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException(
                    "File must have an extension. Accepted: " + ALLOWED_FILE_TYPES.keySet());
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot <= 0 || lastDot == filename.length() - 1) {
            throw new IllegalArgumentException(
                    "File must have a valid extension. Accepted: " + ALLOWED_FILE_TYPES.keySet());
        }

        return filename.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private String detectMimeType(byte[] attachmentBytes, String filename) throws IOException {
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filename);

        try (TikaInputStream stream = TikaInputStream.get(attachmentBytes)) {
            MediaType mediaType = DETECTOR.detect(stream, metadata);
            return mediaType.toString().toLowerCase(Locale.ROOT);
        }
    }

    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket ticket = findById(id);
        if (ticket == null) {
            return null;
        }

        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

        public Ticket updateAssignedStaff(Long id, String staffEmail) {
            Ticket ticket = findById(id);
            if (ticket == null) {
                return null;
            }
            ticket.setAssignedStaff(staffEmail);
            return ticketRepository.save(ticket);
        }

    public Ticket updateInternalNotes(Long id, String notes) {
        Ticket ticket = findById(id);
        if (ticket == null) return null;
        ticket.setInternalNotes(notes);
        return ticketRepository.save(ticket);
    }

    @org.springframework.transaction.annotation.Transactional
    public boolean deleteTicket(Long id) {
        Ticket ticket = findById(id);
        if (ticket == null) {
            return false;
        }
        if (ticket.getSupabaseFilename() != null && !ticket.getSupabaseFilename().isBlank()) {
            supabaseStorageService.deleteFile(ticket.getSupabaseFilename());
        }
        emailMessageRepository.deleteByTicket(ticket);
        ticketRepository.delete(ticket);
        return true;
    }
}



