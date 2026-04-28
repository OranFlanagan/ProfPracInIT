package com.TicketingApp.Controller;

import com.TicketingApp.Entity.Ticket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @Value("${MAX_FILE_SIZE:50MB}")
    private String maxFileSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("maxFileSize", maxFileSize);
        model.addAttribute("errorMessage", "Attachment too big. Please keep file sizes under " + maxFileSize + ".");
        return "email-form";
    }

 @ExceptionHandler(Exception.class)
public String handleGenericException(Exception exc, HttpServletRequest request, Model model) {
    exc.printStackTrace();
    String uri = request.getRequestURI();
    if (uri.startsWith("/admin")) {
        model.addAttribute("errorMessage", exc.getMessage());
        return "error";
    }
    model.addAttribute("ticket", new Ticket());
    model.addAttribute("maxFileSize", maxFileSize);
    model.addAttribute("errorMessage", "An unexpected error occurred. Please try again or contact support.");
    return "email-form";
    }
}