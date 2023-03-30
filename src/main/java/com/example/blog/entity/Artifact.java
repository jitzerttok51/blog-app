package com.example.blog.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
public class Artifact extends BaseEntity {

    private String checksum;
    private String type;
    private String container;
    private String path;
    private long size;

    @Column(name = "file_type")
    private String fileType;

    @OneToMany(mappedBy = "artifact", cascade = CascadeType.REMOVE)
    private Set<FileOwner> owners;
}
