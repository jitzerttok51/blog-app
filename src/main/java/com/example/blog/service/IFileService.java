package com.example.blog.service;

import com.example.blog.dto.ArtifactDTO;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

public interface IFileService {

    ArtifactDTO upload(MultipartFile file);

    Optional<URI> getDownloadURL(long id);

    Optional<ArtifactDTO> getInfo(long id);

    boolean delete(long id);

    Collection<ArtifactDTO> getArtifacts();
}
