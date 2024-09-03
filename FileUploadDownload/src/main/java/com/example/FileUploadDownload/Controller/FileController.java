package com.example.FileUploadDownload.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.FileUploadDownload.UploadFileResponse;
import com.example.FileUploadDownload.Model.FileModel;
import com.example.FileUploadDownload.service.FileStorageService;

@RestController
@RequestMapping("api")
public class FileController {

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		FileModel fileModel = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileModel.getId()).toUriString();
		
		UploadFileResponse uploadFileResponse = new UploadFileResponse();
		uploadFileResponse.setFileName(fileModel.getFileName());
		uploadFileResponse.setFileType(file.getContentType());
		uploadFileResponse.setSize(file.getSize());
		uploadFileResponse.setFileDownloadUri(fileDownloadUri);
		
		
		return uploadFileResponse;
	}
	
	@GetMapping("/downloadFile/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
	    try {
	        // Retrieve file data from the service
	        FileModel fileModel = fileStorageService.getFile(fileId);
	        if (fileModel == null) {
	            return ResponseEntity.notFound().build();
	        }

	        // Create ByteArrayResource for the file data
	        ByteArrayResource resource = new ByteArrayResource(fileModel.getData());

	        // Build and return the ResponseEntity with the file
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(fileModel.getFileType()))
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getFileName() + "\"")
	                .body(resource);

	    } catch (Exception e) {
	        // Log and handle the error
	        // logger.error("Failed to download file", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

}
