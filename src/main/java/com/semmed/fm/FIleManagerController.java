package com.semmed.fm;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FIleManagerController {

    private final FileStorageService service;
    private static final Logger logger = Logger.getLogger(FIleManagerController.class.getName());

    public FIleManagerController(FileStorageService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public boolean uploadFile(@RequestParam MultipartFile file) {
        try {
            service.saveFile(file);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception during upload", e);
        }
        return false;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        logger.log(Level.INFO, "[NORMAL] Downloading file: " + fileName);
        try {
            File downloadFile = service.getDownloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(downloadFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(downloadFile.toPath()))); //slower
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download-faster")
    public ResponseEntity<Resource> downloadFileFaster(@RequestParam("fileName") String fileName) {
        logger.log(Level.INFO, "[FASTER] Downloading file: " + fileName);
        try {
            File fileToDownload = service.getDownloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(fileToDownload)); //faster, because it divides the file into chunks, and downloads in paralel
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
