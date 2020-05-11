package com.virnect.content.infra.file.download;


import org.springframework.http.ResponseEntity;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
public interface FileDownloadService {
    ResponseEntity<byte[]> fileDownload(final String fileName);
}
