package com.virnect.process.infra.file.upload;

/**
 * Project: service-server
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: File Upload Interface for LocalFileUpload and AWS FileUpload
 */
public interface FileUploadService {
	boolean delete(final String url);

	String base64ImageUpload(final String base64Image);

	String getFilePath(String fileName);
}
