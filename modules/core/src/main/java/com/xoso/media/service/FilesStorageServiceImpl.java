package com.xoso.media.service;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.media.model.FileInfo;
import com.xoso.media.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    private final Path root = Paths.get("./files");


    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new AbstractPlatformException("error.msg.media.initialize.folder", "Could not initialize folder for upload!");
        }
    }

    @Override
    public ResultBuilder save(MultipartFile file) {
        try {
            var pathFile = this.root.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.deleteIfExists(pathFile);
            Files.copy(file.getInputStream(), pathFile);
            var fileInfo = FileInfo.builder()
                    .fileName(file.getOriginalFilename())
                    .url(pathFile.toUri().getPath())
                    .build();
            this.fileInfoRepository.saveAndFlush(fileInfo);
            return ResultBuilder.builder().entityId(fileInfo.getId()).build();
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new AbstractPlatformException("error.msg.media.file.name.already.exists", "A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new AbstractPlatformException("error.msg.media.could.not.read.file", "Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            Path file = root.resolve(filename);
            var deleted = Files.deleteIfExists(file);
            if (deleted) {
                var fileInfo = this.fileInfoRepository.findByFileName(filename);
                if (fileInfo != null) {
                    fileInfoRepository.delete(fileInfo);
                }
            }
            return deleted;
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new AbstractPlatformException("error.msg.media.could.not.load.file", "Could not load the files!");
        }
    }
}
