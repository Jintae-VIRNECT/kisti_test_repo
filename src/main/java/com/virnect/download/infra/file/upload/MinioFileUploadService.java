package com.virnect.download.infra.file.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.common.io.Files;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.error.ErrorCode;

@Slf4j
@Service
@Profile({"local", "develop", "onpremise"})
@RequiredArgsConstructor
public class MinioFileUploadService implements FileUploadService {
	private final MinioClient minioClient;
	@Value("${minio.bucket:virnect-download}")
	private String bucket;
	@Value("${minio.resource:app}")
	private String resourceDir;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	@Override
	public String upload(MultipartFile file) throws IOException {
		log.info("[MINIO UPLOADER] - UPLOAD BEGIN");
		if (file.getSize() <= 0) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_EMPTY_APPLICATION_FILE);
		}

		// 1. 확장자 검사
		String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
		if (!allowedExtension.contains(fileExtension)) {
			log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT);
		}

		log.info("UPLOAD SERVICE: ==> originName: [{}] , size: {}", file.getOriginalFilename(), file.getSize());

		String fileName = String.format(
			"%s/%s", resourceDir, file.getOriginalFilename());

		log.info("[MINIO_UPLOAD_BEGIN]");
		log.info("BUCKET: [{}], FILENAME: [{}]", bucket, fileName);

		Map<String, String> contentMetadata = new HashMap<>();
		contentMetadata.put("Content-Disposition",String.format("attachment; filename=\"%s\"", file.getOriginalFilename()));
		contentMetadata.put("filename",file.getOriginalFilename());

		PutObjectArgs putObjectArgs = PutObjectArgs.builder()
			.contentType(file.getContentType())
			.bucket(bucket)
			.object(fileName)
			.stream(file.getInputStream(), file.getInputStream().available(), -1)
			.headers(contentMetadata)
			.build();
		try {
			minioClient.putObject(putObjectArgs);
			String url = minioClient.getObjectUrl(bucket, fileName);
			log.info("[MINIO_UPLOAD_URL] - [{}]", url);
			log.info("[MINIO_UPLOAD_END]");
			return url;
		} catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
			log.error("[MINIO_FILE_UPLOAD_ERROR] - {}", e.getMessage(), e);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
	}

	@Override
	public boolean delete(String url) {
		return false;
	}
}
