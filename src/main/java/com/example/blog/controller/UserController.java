package com.example.blog.controller;

import com.example.blog.dto.ArtifactDTO;
import com.example.blog.dto.FileEditDTO;
import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.IUserFileService;
import com.example.blog.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    private final IUserFileService fileService;

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserCreateDTO userCreate) {
        var result = userService.register(userCreate);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }

    @GetMapping
    public Collection<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable long id) {
        return ResponseEntity.of(userService.getUser(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable long id) {
        return ResponseEntity.of(userService.deleteUser(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDTO> editUser(@PathVariable long id, @RequestBody UserCreateDTO userCreate) {
        return ResponseEntity.of(userService.editUser(id, userCreate));
    }

    @GetMapping("{id}/files")
    public ResponseEntity<Set<ArtifactDTO>> getUserFiles(@PathVariable long id) {
        return ResponseEntity.ok(fileService.userGetAllFiles(id));
    }

    @GetMapping("{id}/files/{fileId}/info")
    public ResponseEntity<ArtifactDTO> getUserFileInfo(@PathVariable long id, @PathVariable long fileId) {
        return ResponseEntity.of(fileService.userGetInfo(id, fileId));
    }

    @PutMapping("{id}/files/{fileId}")
    public ResponseEntity<ArtifactDTO> editUserFile(@PathVariable long id, @PathVariable long fileId,
        @RequestBody FileEditDTO edit) {
        return ResponseEntity.of(fileService.userEditFile(id, fileId, edit));
    }

    @DeleteMapping("{id}/files/{fileId}")
    public ResponseEntity<ArtifactDTO> deleteUserFile(@PathVariable long id, @PathVariable long fileId) {
        fileService.userDeleteFile(id, fileId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/files")
    public ResponseEntity<ArtifactDTO> createUserFile(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        var result = fileService.uploadFile(file);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }
}
