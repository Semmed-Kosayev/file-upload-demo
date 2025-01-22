package com.semmed.fm;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
}
