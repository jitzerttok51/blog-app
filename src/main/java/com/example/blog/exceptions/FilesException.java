package com.example.blog.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FilesException extends BlogAppException {

    public FilesException(String message, Throwable t, HttpStatus status) {
        super(message, t, status);
    }

    public FilesException(Throwable t, HttpStatus status) {
        super(t, status);
    }

    public FilesException(String message, HttpStatus status) {
        super(message, status);
    }
}