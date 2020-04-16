package com.virnect.content.infra.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Project: base
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class AmazonS3UploadService implements FileUploadService, FileIOService {
    @Override
    public String upload(MultipartFile file) {
        return null;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException {
        return null;
    }

    @Override
    public boolean delete(String url) {
        return false;
    }

    @Override
    public String getFileExtension(String originFileName) {
        return null;
    }

    @Override
    public boolean isAllowFileExtension(String fileExtension) {
        return false;
    }

    @Override
    public File getFile(String url) {
        return null;
    }

    @Override
    public String base64ImageUpload(String base64Image) {
        return null;
    }

    @Override
    public boolean copyFileWithUrl(final String sourceUrl, final String destinationUrl) {
        return false;
    }

    @Override
    public boolean copyFileWithFile(final File sourceFile, final String destinationUrl) {
        return false;
    }

    @Override
    public boolean rename(final String sourceUrl, final String destinationUrl) {
        return false;
    }
}
