package com.example.blog.service.impl;

import com.example.blog.exceptions.FilesException;
import com.example.blog.service.IFileContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
@Service
public class LocalFileStorageImpl implements IFileContainer {

    private final Path storage;

    private final String baseURL;

    private LocalFileStorageImpl(
        @Value("${storage.local.location}") String pathRaw,
        @Value("${storage.local.url}") String baseUrl) {
        baseURL = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        storage = Path.of(pathRaw.replace("file:", ""));
    }

    @Override
    public String container() {
        return LOCAL_STORAGE;
    }

    @Override
    public Path saveFile(Path location) {
        try {
            Files.createDirectories(storage);
            var path = storage.resolve(location.getFileName());
            return Files.copy(location, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FilesException("Cannot save file "+location, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<URI> getDownloadURL(Path location) {
        return Optional.of(URI.create(baseURL+location.getFileName()));
    }

    @Override
    public void deleteFile(Path location) {
        try {
            Files.createDirectories(storage);
            var path = storage.resolve(location.getFileName());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Cannot delete file ", e);
        }
    }
}
