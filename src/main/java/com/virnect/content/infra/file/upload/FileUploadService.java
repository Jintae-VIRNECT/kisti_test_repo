package com.virnect.content.infra.file.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * Project: service-server
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: File Upload Interface for LocalFileUpload and AWS FileUpload
 */
public interface FileUploadService {

	void deleteByFileUrl(String fileUrl);

	String uploadByBase64Image(String base64Image, String fileDir, String fileName);

	String uploadByFileInputStream(MultipartFile file, String fileDir, String fileNameWithoutExtension);

	String copyByFileObject(String sourceFileUrl, String destinationFileDir, String destinationFileNameWithoutExtension);
}
