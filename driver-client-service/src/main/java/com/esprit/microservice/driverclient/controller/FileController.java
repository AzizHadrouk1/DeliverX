package com.esprit.microservice.driverclient.controller;

import com.esprit.microservice.driverclient.model.StoredFile;
import com.esprit.microservice.driverclient.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public StoredFile upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "document") String category,
            @RequestParam(required = false) String ownerType,
            @RequestParam(required = false) Long ownerId) {
        return fileStorageService.store(file, category, ownerType, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        StoredFile storedFile = fileStorageService.findById(id);
        Resource resource = fileStorageService.loadAsResource(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + storedFile.getOriginalName() + "\"")
                .body(resource);
    }

    @GetMapping("/owner/{ownerType}/{ownerId}")
    public List<StoredFile> listByOwner(@PathVariable String ownerType, @PathVariable Long ownerId) {
        return fileStorageService.findByOwner(ownerType, ownerId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        fileStorageService.delete(id);
    }
}
