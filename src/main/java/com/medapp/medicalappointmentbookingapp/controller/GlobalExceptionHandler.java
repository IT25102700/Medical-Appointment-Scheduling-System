package com.medapp.medicalappointmentbookingapp.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, Model model) {
        model.addAttribute("error", "The file you are trying to upload is too large! Please use an image smaller than 10MB.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception exc, Model model) {
        model.addAttribute("error", "An unexpected error occurred: " + exc.getMessage());
        return "error";
    }
}
