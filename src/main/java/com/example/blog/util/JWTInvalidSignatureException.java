package com.example.blog.util;

public class JWTInvalidSignatureException extends Exception {

    JWTInvalidSignatureException(String message) {
        super(message);
    }

    JWTInvalidSignatureException(String message, Throwable t) {
        super(message, t);
    }
}
