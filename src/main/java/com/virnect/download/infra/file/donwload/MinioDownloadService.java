package com.virnect.download.infra.file.donwload;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"onpremise", "test"})
@Service
@RequiredArgsConstructor
public class MinioDownloadService implements FileDownloadService {
	private final MinioClient minioClient;
}
