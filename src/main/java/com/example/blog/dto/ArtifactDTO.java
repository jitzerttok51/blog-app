package com.example.blog.dto;

import com.example.blog.constants.FileVisibility;
import com.example.blog.entity.Artifact;
import com.example.blog.entity.FileOwner;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
public class ArtifactDTO {

    private long id;
    private String sha256;
    private String type;
    private String container;
    private URI uri;
    private long size;
    private String fileType;

    private String owner;
    private FileVisibility visibility;

    public ArtifactDTO(FileOwner artifact) {
        populate(artifact.getArtifact());
        this.id = artifact.getId();
        this.owner = artifact.getOwner().getUsername();
        this.visibility = artifact.getVisibility();
    }

    private void populate(Artifact artifact) {
        this.sha256 = artifact.getChecksum();
        this.type = artifact.getType();
        this.container = artifact.getContainer();
        this.size = artifact.getSize();
        this.fileType = artifact.getFileType();
    }
}
