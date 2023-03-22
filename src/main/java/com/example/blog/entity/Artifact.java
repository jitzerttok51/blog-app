package com.example.blog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
@RequiredArgsConstructor
public class Artifact extends BaseEntity {

    private String sha256;
    private String type;
    private String container;
    private String name;
    private long size;

    @Column(name = "file_type")
    private String fileType;
}
