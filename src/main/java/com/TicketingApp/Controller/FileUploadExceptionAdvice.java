package com.TicketingApp.Controller;

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
        model.addAttribute("errorMessage", "Attachment too big. Please keep file sizes under " + maxFileSize + ".");
        return "email-form";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception exc, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again or contact support.");
        return "email-form";
    }
}
