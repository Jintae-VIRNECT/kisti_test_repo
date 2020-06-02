package com.virnect.content.infra.file.download;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */

@Slf4j
public class LocalFileDownloadService implements FileDownloadService {

    @Value("${upload.dir}")
    private String uploadPath;

    @Override
    public ResponseEntity<byte[]> fileDownload(String path) {
        try {
            String regex = "/";
            String[] parts = path.split(regex);
            String fileName = parts[parts.length - 1];
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());
            byte[] bytes = IOUtils.toByteArray(resource.getURI());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(resource.getFile().length());
            httpHeaders.setContentDispositionFormData("attachment", resource.getFilename());
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("FILE LOAD ERROR - FILE NAME: [{}]", path);
            log.error("FILE LOAD ERROR - MESSAGE: [{}]", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }
    }

    private Path load(final String fileName) {
        Path rootLocation = Paths.get(uploadPath);
        return rootLocation.resolve(fileName);
    }

    @Override
    public void copyFileS3ToLocal(String fileName) {}
}
