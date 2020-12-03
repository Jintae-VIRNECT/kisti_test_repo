package com.virnect.download.infra.file.donwload;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Profile({ "onpremise"})
@Service
@RequiredArgsConstructor
public class MinioDownloadService implements FileDownloadService {
    private final MinioClient minioClient;
    @Value("${minio.bucket:virnect-download}")
    private String bucket;
    @Value("${minio.resource:app/}")
    private String resourceDir;

    @Override
    public byte[] fileDownload(String fileName) throws IOException {
        return new byte[0];
    }

    @Override
    public String fileUpload(MultipartFile multipartFile, String fileName) throws IOException {
        return "";
    }
}
