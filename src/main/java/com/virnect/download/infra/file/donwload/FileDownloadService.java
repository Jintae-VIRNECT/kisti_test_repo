package com.virnect.download.infra.file.donwload;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileDownloadService {
	byte[] fileDownload(String fileName) throws IOException;
	String fileUpload(MultipartFile multipartFile, String fileName) throws IOException;
}
