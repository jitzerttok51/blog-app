package com.example.blog.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BlogAppException extends RuntimeException {

    private final HttpStatus status;

    public BlogAppException(String message, Throwable t, HttpStatus status) {
        super(message, t);
        this.status = status;
    }

    public BlogAppException(Throwable t, HttpStatus status) {
        super(t);
        this.status = status;
    }

    public BlogAppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
