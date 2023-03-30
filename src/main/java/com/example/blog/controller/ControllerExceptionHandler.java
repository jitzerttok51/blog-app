package com.example.blog.controller;

import com.example.blog.dto.ExceptionDTO;
import com.example.blog.dto.ViolationDTO;
import com.example.blog.exceptions.BlogAppException;
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

    @ExceptionHandler(BlogAppException.class)
    public ResponseEntity<ExceptionDTO> handleValidationExceptions(BlogAppException ex) {
        return new ExceptionDTO(ex).response();
    }
}
