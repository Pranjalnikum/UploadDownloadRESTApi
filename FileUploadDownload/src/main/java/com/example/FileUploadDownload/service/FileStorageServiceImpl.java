package com.example.FileUploadDownload.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.FileUploadDownload.Exception.FileStorageException;
import com.example.FileUploadDownload.Exception.MyFileNotFoundException;
import com.example.FileUploadDownload.Model.FileModel;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    // Declare the in-memory store
    private Map<String, FileModel> fileStore = new HashMap<>();
    private long idCounter = 1;

    @Override
    public FileModel storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! your file contains invalid characters...");
            }

            FileModel fileModel = new FileModel();
            fileModel.setId(String.valueOf(idCounter++));
            fileModel.setFileName(fileName);
            fileModel.setFileType(file.getContentType());
            fileModel.setData(file.getBytes());

            // Store the file in the in-memory store
            fileStore.put(fileModel.getId(), fileModel);

            return fileModel;

        } catch (IOException ioe) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again.", ioe);
        }
    }

    @Override
    public FileModel getFile(String fileId) {
        return Optional.ofNullable(fileStore.get(fileId))
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }
}
