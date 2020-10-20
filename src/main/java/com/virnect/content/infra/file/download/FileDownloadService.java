package com.virnect.content.infra.file.download;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
public interface FileDownloadService {
	ResponseEntity<byte[]> fileDownload(final String fileName);

	void copyFileS3ToLocal(String fileName);

	String getFilePath(String bucketResource, String fileName);

	MultipartFile getMultipartfile(String fileName);
}
