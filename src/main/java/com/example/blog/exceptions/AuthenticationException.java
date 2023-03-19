package com.example.blog.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationException extends RuntimeException {

    private final HttpStatus status;

    public AuthenticationException(String message, Throwable t, HttpStatus status) {
        super(message, t);
        this.status = status;
    }

    public AuthenticationException(Throwable t, HttpStatus status) {
        super(t);
        this.status = status;
    }

    public AuthenticationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
