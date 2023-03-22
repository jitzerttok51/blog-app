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

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesController {

    private final IFileService fileService;

    @PostMapping
    public ArtifactDTO upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }

    @GetMapping
    public Collection<ArtifactDTO> getFiles() {
        return fileService.getArtifacts();
    }

    @GetMapping("{id}")
    public ResponseEntity<URI> getFile(@PathVariable long id) {
        var opt = fileService.getDownloadURL(id);
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
        return ResponseEntity.of(fileService.getInfo(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        var result = fileService.delete(id);
        return ResponseEntity.status(result ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).build();
    }
}
