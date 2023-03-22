package com.example.blog.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FilesException extends RuntimeException {

    private final HttpStatus status;

    public FilesException(String message, Throwable t, HttpStatus status) {
        super(message, t);
        this.status = status;
    }

    public FilesException(Throwable t, HttpStatus status) {
        super(t);
        this.status = status;
    }

    public FilesException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}