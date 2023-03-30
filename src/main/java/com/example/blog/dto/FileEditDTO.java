package com.example.blog.dto;

import com.example.blog.constants.FileVisibility;
import lombok.Setter;

import java.util.Optional;

@Setter
public class FileEditDTO {

    private FileVisibility visibility;

    public Optional<FileVisibility> getVisibility() {
        return Optional.ofNullable(visibility);
    }
}
