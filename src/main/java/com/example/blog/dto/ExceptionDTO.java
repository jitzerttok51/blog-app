package com.example.blog.dto;

import com.example.blog.exceptions.BlogAppException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.PrintWriter;
import java.io.StringWriter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDTO {

    private final HttpStatus status;

    private final String message;

    private final String stackTrace;

    public ExceptionDTO(BlogAppException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        stackTrace = pw.toString();
    }

    public ResponseEntity<ExceptionDTO> response() {
        return ResponseEntity.status(status).body(this);
    }
}
