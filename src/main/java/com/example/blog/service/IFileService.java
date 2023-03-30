package com.example.blog.service;

import com.example.blog.dto.ArtifactDTO;
import com.example.blog.dto.FileEditDTO;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

public interface IFileService {

    ArtifactDTO uploadFile(MultipartFile file);

    Optional<URI> getFileDownloadURL(long id);

    Optional<ArtifactDTO> getFileInfo(long id);

    void deleteFile(long id);

    void editFile(long fileId, FileEditDTO edit);

    Collection<ArtifactDTO> getFiles();
}
