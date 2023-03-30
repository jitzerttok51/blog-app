package com.example.blog.controller;

import com.example.blog.dto.ArtifactDTO;
import com.example.blog.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Collection;

import static com.example.blog.constants.URLConstants.ENDPOINT_FILES;

@RestController
@RequestMapping(ENDPOINT_FILES)
@RequiredArgsConstructor
public class FilesController {

    private final IFileService fileService;

    @PostMapping
    public ResponseEntity<ArtifactDTO> upload(@RequestParam("file") MultipartFile file) {
        var result = fileService.uploadFile(file);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }

    @GetMapping
    public Collection<ArtifactDTO> getFiles() {
        return fileService.getFiles();
    }

    @GetMapping("{id}")
    public ResponseEntity<URI> getFile(@PathVariable long id) {
        var opt = fileService.getFileDownloadURL(id);
        if (opt.isEmpty()) {
            return ResponseEntity
                .notFound()
                .build();
        }

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(opt.get())
            .build();
    }

    @GetMapping("{id}/info")
    public ResponseEntity<ArtifactDTO> getFileInfo(@PathVariable long id) {
        return ResponseEntity.of(fileService.getFileInfo(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        fileService.deleteFile(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
