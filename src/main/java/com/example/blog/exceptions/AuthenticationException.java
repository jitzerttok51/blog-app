package com.example.blog.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BlogAppException {

    public AuthenticationException(String message, Throwable t, HttpStatus status) {
        super(message, t, status);
    }

    public AuthenticationException(Throwable t, HttpStatus status) {
        super(t, status);
    }

    public AuthenticationException(String message, HttpStatus status) {
        super(message, status);
    }
}
