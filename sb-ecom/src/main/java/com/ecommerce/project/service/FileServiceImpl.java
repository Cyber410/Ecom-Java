package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // Generate a unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null ? 
            originalFileName.substring(originalFileName.lastIndexOf('.')) : "";
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // Save the file to the specified path
        File targetFile = new File(path + File.separator + fileName);
        File parentDir = targetFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        file.transferTo(targetFile);
        return fileName;
    }
}
