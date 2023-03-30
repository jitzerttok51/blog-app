package com.example.blog.service;

import com.example.blog.dto.ArtifactDTO;
import com.example.blog.dto.FileEditDTO;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

public interface IUserFileService {

    ArtifactDTO uploadFile(MultipartFile file);

    Optional<URI> userGetDownloadURL(long userId, long fileId);

    Optional<ArtifactDTO> userGetInfo(long userId, long fileId);

    Set<ArtifactDTO> userGetAllFiles(long userId);

    void userDeleteFile(long userId, long fileId);

    Optional<ArtifactDTO> userEditFile(long userId, long fileId, FileEditDTO edit);
}
