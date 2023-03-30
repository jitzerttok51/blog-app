package com.example.blog.service.impl;

import com.example.blog.constants.FileVisibility;
import com.example.blog.dto.ArtifactDTO;
import com.example.blog.dto.FileEditDTO;
import com.example.blog.entity.Artifact;
import com.example.blog.entity.FileOwner;
import com.example.blog.entity.User;
import com.example.blog.exceptions.FilesException;
import com.example.blog.repositories.ArtifactsRepository;
import com.example.blog.repositories.FileOwnerRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.service.IFileContainer;
import com.example.blog.service.IFileService;
import com.example.blog.service.IUserFileService;
import com.example.blog.util.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FileServiceImpl implements IFileService, IUserFileService {

    private final ArtifactsRepository repository;

    private final UserRepository userRepository;

    private final FileOwnerRepository fileOwnerRepository;

    private final Map<String, IFileContainer> containers = new HashMap<>();

    private Path tempDir;

    public FileServiceImpl(ArtifactsRepository repository, List<IFileContainer> containers,
        UserRepository userRepository, FileOwnerRepository fileOwnerRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.fileOwnerRepository = fileOwnerRepository;
        containers.forEach(c -> this.containers.put(c.container(), c));
    }

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        tempDir = Files.createTempDirectory("files-");
        deleteUnownedFiles();
    }

    @Override
    @Transactional
    public ArtifactDTO uploadFile(MultipartFile file) {
        try {

            var user = getCurrentUser().orElseThrow(() ->
                         new FilesException("Cannot find user", HttpStatus.INTERNAL_SERVER_ERROR));

            var art = createArtifact(file);

            var map = fileOwnerRepository.findAllByChecksum(art.getChecksum());

            // File exists and is already owned by the user
            if(map.containsKey(user.getUsername())) {
                return toDTO(map.get(user.getUsername()));
            }

            // File exists but it is not owned by the user
            if(!map.isEmpty()) {
                art = map.values().iterator().next().getArtifact();
            } else {
                // Save the artifact to storage
                saveToStorage(art);
            }

            var owner = new FileOwner();
            owner.setOwner(user);
            owner.setVisibility(FileVisibility.PRIVATE);

            art = repository.save(art);
            owner.setArtifact(art);
            owner = fileOwnerRepository.save(owner);
            return toDTO(owner);
        } catch (IOException e) {
            throw new FilesException("Cannot save file ", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveToStorage(Artifact artifact) {
        var container = containers.get(artifact.getContainer());
        var dest = container.saveFile(Path.of(artifact.getPath()));
        artifact.setPath(dest.toString());
    }

    private Artifact createArtifact(MultipartFile file) throws IOException {
        var temp = copyToTemp(file);
        var fileType = file.getContentType();
        if(fileType == null) {
            fileType = "application/octet-stream";
        }
        var type = fileType.substring(0, fileType.indexOf("/"));
        var ext = fileType.substring(fileType.indexOf("/")+1);
        var sha = FileUtils.calculateSha256(temp);
        var name = sha+"."+ext;
        var tempNewName = Files.move(temp, temp.getParent().resolve(name), StandardCopyOption.REPLACE_EXISTING);
        var size = Files.size(tempNewName);

        return new Artifact(sha, type, getDefaultContainer(),
            tempNewName.toAbsolutePath().toString(), size, fileType, Set.of());
    }

    @Override
    public Set<ArtifactDTO> userGetAllFiles(long userId) {
        return fileOwnerRepository
            .findAllByUser(userId)
            .filter(this::verifyReadPermissions)
            .map(this::toDTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ArtifactDTO> userGetInfo(long userId, long fileId) {
        return fileOwnerRepository
            .findSingleByUser(userId, fileId)
            .filter(this::verifyReadPermissions)
            .map(this::toDTO);
    }

    @Override
    public Optional<URI> userGetDownloadURL(long userId, long fileId) {
        return userGetInfo(userId, fileId)
               .map(ArtifactDTO::getUri);
    }

    @Override
    @Transactional
    public void userDeleteFile(long userId, long fileId) {
        var file = fileOwnerRepository
            .findSingleByUser(userId, fileId)
            .filter(this::verifyWritePermissions)
            .orElseThrow(this::fileNotFound);

        fileOwnerRepository.delete(file);
    }

    @Override
    @Transactional
    public Optional<ArtifactDTO> userEditFile(long userId, long fileId, FileEditDTO edit) {
        var file  = fileOwnerRepository
            .findById(fileId)
            .filter(this::verifyWritePermissions)
            .orElseThrow(this::fileNotFound);

        edit.getVisibility().ifPresent(file::setVisibility);

        file = fileOwnerRepository.save(file);
        return Optional.of(toDTO(file));
    }

    @Override
    @Transactional
    public void editFile(long fileId, FileEditDTO edit) {
        var file  = fileOwnerRepository
            .findById(fileId)
            .orElseThrow(this::fileNotFound);

        edit.getVisibility().ifPresent(file::setVisibility);

        fileOwnerRepository.save(file);
    }

    private Optional<String> getCurrentUsername() {
        return Optional.ofNullable(
            SecurityContextHolder.getContext().getAuthentication()
        ).map(Authentication::getName);
    }

    private Optional<User> getCurrentUser() {
        var auth = SecurityContextHolder
            .getContext().getAuthentication();

        if(auth != null) {
           return userRepository.findUserByUsername(auth.getName());
        }

        return Optional.empty();
    }

    private ArtifactDTO toDTO(FileOwner owner) {
        var dto = new ArtifactDTO(owner);
        getDownloadURL(owner.getArtifact()).ifPresent(dto::setUri);
        return dto;
    }

    @Override
    public Optional<URI> getFileDownloadURL(long id) {
        return getFileInfo(id).map(ArtifactDTO::getUri);
    }

    @Override
    public Optional<ArtifactDTO> getFileInfo(long id) {
        return fileOwnerRepository
            .findById(id)
            .map(this::toDTO);
    }

    @Override
    @Transactional
    public void deleteFile(long id) {
        var file  = fileOwnerRepository
            .findById(id)
            .orElseThrow(this::fileNotFound);

        fileOwnerRepository.delete(file);
    }

    private FilesException fileNotFound() {
        return new FilesException("File not found", HttpStatus.NOT_FOUND);
    }

    private boolean verifyPermissions(FileOwner file, boolean write) {
        var ownerUsername = file.getOwner().getUsername();
        var visibility = file.getVisibility();
        var isOwner = getCurrentUsername()
            .map(ownerUsername::equals)
            .orElse(false);

        var readPermissions = visibility == FileVisibility.PUBLIC || isOwner;

        return write ? isOwner : readPermissions;
    }

    private boolean verifyReadPermissions(FileOwner file) {
        return verifyPermissions(file, false);
    }

    private boolean verifyWritePermissions(FileOwner file) {
        return verifyPermissions(file, true);
    }

    @Override
    public Collection<ArtifactDTO> getFiles() {
        return fileOwnerRepository
            .findAll().stream().map(this::toDTO).toList();
    }

    private Path copyToTemp(MultipartFile multipartFile) {
        try {
            var dest = Files.createTempFile(tempDir, "file-", ".n");
            multipartFile.transferTo(dest);
            return dest;
        } catch (IOException e) {
            throw new FilesException("Cannot save file", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getDefaultContainer() {
        return IFileContainer.LOCAL_STORAGE;
    }

    private void deleteFile (Artifact artifact) {
        containers.get(artifact.getContainer()).deleteFile(Path.of(artifact.getPath()));
    }

    private Optional<URI> getDownloadURL(Artifact artifact) {
        return containers.get(artifact.getContainer()).getDownloadURL(Path.of(artifact.getPath()));
    }

    @Transactional
    public void deleteUnownedFiles() {
        var files = repository.getUnownedFiles();
        files.forEach(f-> {
            deleteFile(f);
            repository.delete(f);
        });
    }
}
