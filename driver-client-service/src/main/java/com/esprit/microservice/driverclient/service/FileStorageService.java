package com.esprit.microservice.driverclient.service;

import com.esprit.microservice.driverclient.model.StoredFile;
import com.esprit.microservice.driverclient.repository.StoredFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "application/pdf");

    private final StoredFileRepository storedFileRepository;
    private final Path uploadDir;

    public FileStorageService(StoredFileRepository storedFileRepository,
                              @Value("${file.upload-dir}") String uploadDir,
                              @Value("${file.max-size}") long maxSize) {
        this.storedFileRepository = storedFileRepository;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxSize = maxSize;
        initUploadDir();
    }

    private final long maxSize;

    private void initUploadDir() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create upload directory");
        }
    }

    public StoredFile store(MultipartFile file, String category, String ownerType, Long ownerId) {
        validate(file);

        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = uploadDir.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }

        StoredFile storedFile = new StoredFile();
        storedFile.setOriginalName(file.getOriginalFilename());
        storedFile.setStoredName(storedName);
        storedFile.setContentType(file.getContentType());
        storedFile.setSize(file.getSize());
        storedFile.setCategory(category);
        storedFile.setOwnerType(ownerType);
        storedFile.setOwnerId(ownerId);
        return storedFileRepository.save(storedFile);
    }

    @Transactional(readOnly = true)
    public StoredFile findById(Long id) {
        return storedFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<StoredFile> findByOwner(String ownerType, Long ownerId) {
        return storedFileRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }

    @Transactional(readOnly = true)
    public Resource loadAsResource(Long id) {
        StoredFile storedFile = findById(id);
        try {
            Path filePath = uploadDir.resolve(storedFile.getStoredName());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found on disk");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid file path");
        }
    }

    public void delete(Long id) {
        StoredFile storedFile = findById(id);
        try {
            Files.deleteIfExists(uploadDir.resolve(storedFile.getStoredName()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file");
        }
        storedFileRepository.delete(storedFile);
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File exceeds maximum size");
        }
        if (file.getContentType() == null || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File type not allowed");
        }
    }
}
