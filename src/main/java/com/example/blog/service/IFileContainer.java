package com.example.blog.service;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

public interface IFileContainer {

    String LOCAL_STORAGE = "local";

    String container();

    Path saveFile(Path location);

    Optional<URI> getDownloadURL(Path location);

    void deleteFile(Path location);
}
