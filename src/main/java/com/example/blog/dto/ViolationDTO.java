package com.example.blog.dto;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

@Getter
public class ViolationDTO {

    private final String property;
    private final String message;
    private final String type;

    public ViolationDTO(ConstraintViolation<?> violation) {
        this.property = violation.getPropertyPath().toString();
        this.type = property.isBlank() ? "object" : "property";
        this.message = violation.getMessage();
    }
}
