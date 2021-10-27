package com.virnect.content.infra.file.download;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
public interface FileDownloadService {
	ResponseEntity<byte[]> fileDownload(final String fileUrl, @Nullable String range);

	String getDefaultImagePath(String bucketResource, String fileName);

	long getFileSize(String fileUrl);

	byte[] getFileStreamBytes(String fileUrl);

	byte[] multipleFileDownload(List<String> fileUrlList);
}
