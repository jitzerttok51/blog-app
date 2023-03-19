package com.example.blog.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class NotValidException extends RuntimeException {

    private final Set<? extends ConstraintViolation<?>> validations;
}
