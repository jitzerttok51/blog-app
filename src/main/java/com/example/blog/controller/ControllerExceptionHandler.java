package com.example.blog.controller;

import com.example.blog.dto.ViolationDTO;
import com.example.blog.exceptions.NotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<List<ViolationDTO>> handleValidationExceptions(NotValidException ex) {
        var violation = ex.getValidations().stream().map(ViolationDTO::new).toList();
        return ResponseEntity.badRequest().body(violation);
    }
}
