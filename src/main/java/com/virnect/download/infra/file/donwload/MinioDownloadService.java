package com.virnect.download.infra.file.donwload;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;

@Profile({"local", "develop", "onpremise"})
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
}
