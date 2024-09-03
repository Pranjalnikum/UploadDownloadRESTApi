package com.example.FileUploadDownload.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.FileUploadDownload.Model.FileModel;


public interface FileStorageService {

	public FileModel storeFile(MultipartFile file);

	public FileModel getFile(String fileId);

}
