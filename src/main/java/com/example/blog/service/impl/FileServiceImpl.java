package com.example.blog.service.impl;

import com.example.blog.dto.ArtifactDTO;
import com.example.blog.entity.Artifact;
import com.example.blog.exceptions.FilesException;
import com.example.blog.repositories.ArtifactsRepository;
import com.example.blog.service.IFileContainer;
import com.example.blog.service.IFileService;
import com.example.blog.util.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {

    private final ArtifactsRepository repository;

    private final ModelMapper mapper;

    private final Map<String, IFileContainer> containers = new HashMap<>();

    private Path tempDir;

    public FileServiceImpl(ArtifactsRepository repository, List<IFileContainer> containers, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        containers.forEach(c -> this.containers.put(c.container(), c));
    }

    @PostConstruct
    public void init() throws IOException {
        tempDir = Files.createTempDirectory("files-");
    }

    @Override
    @Transactional
    public ArtifactDTO upload(MultipartFile file) {
        try {
            var temp = copyToTemp(file);
            var fileType = file.getContentType();
            if(fileType == null) {
                fileType = "application/octet-stream";
            }
            var type = fileType.substring(0, fileType.indexOf("/"));
            var ext = fileType.substring(fileType.indexOf("/")+1);
            var sha = FileUtils.calculateSha256(temp);
            var name = sha+"."+ext;
            var tempNewName = Files.move(temp, temp.getParent().resolve(name));
            var size = Files.size(tempNewName);
            var dest = getDefaultContainer().saveFile(tempNewName);

            var opt = repository.findByChecksum(sha);
            if(opt.isPresent()) {
                return toDTO(opt.get());
            }

            var artifact = new Artifact();
            artifact.setFileType(fileType);
            artifact.setContainer(getDefaultContainer().container());
            artifact.setType(type);
            artifact.setSha256(sha);
            artifact.setSize(size);
            artifact.setName(dest.toString());

            artifact = repository.save(artifact);
            return toDTO(artifact);
        } catch (IOException e) {
            throw new FilesException("Cannot save file ", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ArtifactDTO toDTO(Artifact artifact) {
        var dto = mapper.map(artifact, ArtifactDTO.class);
        getDefaultContainer().getDownloadURL(Path.of(artifact.getName())).ifPresent(dto::setUri);
        return dto;
    }

    @Override
    public Optional<URI> getDownloadURL(long id) {
        return getInfo(id).map(ArtifactDTO::getUri);
    }

    @Override
    public Optional<ArtifactDTO> getInfo(long id) {
        return repository.findById(id).map(this::toDTO);
    }

    @Override
    public boolean delete(long id) {
        var opt  = repository.findById(id);
        if(opt.isPresent()) {
            var art = opt.get();
            getDefaultContainer().deleteFile(Path.of(art.getName()));
            repository.delete(art);
            return true;
        }
        return false;
    }

    @Override
    public Collection<ArtifactDTO> getArtifacts() {
        return repository.findAll().stream().map(this::toDTO).toList();
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

    private IFileContainer getDefaultContainer() {
        return containers.get(IFileContainer.LOCAL_STORAGE);
    }
}
