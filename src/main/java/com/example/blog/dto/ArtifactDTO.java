package com.example.blog.dto;

import lombok.Data;

import java.net.URI;

@Data
public class ArtifactDTO {

    private long id;
    private String sha256;
    private String type;
    private String container;
    private URI uri;
    private long size;
    private String fileType;
}
