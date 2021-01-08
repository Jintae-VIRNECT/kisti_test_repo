package com.virnect.download.infra.file.donwload;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileDownloadService {
	byte[] fileDownload(String fileName) throws IOException;
	String fileUpload(MultipartFile multipartFile, String fileName) throws IOException;
}
