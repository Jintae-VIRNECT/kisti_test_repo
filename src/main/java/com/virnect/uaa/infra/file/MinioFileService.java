package com.virnect.uaa.infra.file;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;

@Slf4j
@Service
@Profile(value = {"dev", "local", "develop", "onpremise", "test"})
@RequiredArgsConstructor
public class MinioFileService implements FileService {
	private final MinioClient minioClient;
	@Value("${minio.bucket:virnect-platform}")
	private String bucket;
	@Value("${minio.resource:user/profile}")
	private String resourceDir;

	@Override
	public String upload(MultipartFile file) throws IOException {
		final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;
		final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");

		// 1. 빈 파일 여부 확인
		if (file.getSize() == 0) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
		}

		// 2. 확장자 검사
		String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
		if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
			log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_EXTENSION);
		}

		// 3. 파일 용량 검사
		if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_SIZE_LIMIT);
		}

		log.info("UPLOAD SERVICE: ==> originName: [{}] , size: {}", file.getOriginalFilename(), file.getSize());

		String fileName = String.format(
			"%s/%s_%s.%s", resourceDir, LocalDate.now(), RandomStringUtils.randomAlphabetic(20), fileExtension
		);

		log.info("[MINIO_UPLOAD_BEGIN]");
		log.info("BUCKET: [{}], FILENAME: [{}]", bucket, fileName);

		PutObjectArgs putObjectArgs = PutObjectArgs.builder()
			.contentType(file.getContentType())
			.bucket(bucket)
			.object(fileName)
			.stream(file.getInputStream(), file.getInputStream().available(), -1)
			.build();
		try {
			minioClient.putObject(putObjectArgs);
			String url = minioClient.getObjectUrl(bucket, fileName);
			log.info("[MINIO_UPLOAD_URL] - [{}]", url);
			log.info("[MINIO_UPLOAD_END]");
			return url;
		} catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
			log.error("[MINIO_FILE_UPLOAD_ERROR] - {}", e.getMessage(), e);
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
		}
	}

	@Override
	public String upload(MultipartFile file, String fileName) throws IOException {
		return null;
	}

	@Override
	public boolean delete(String url) {
		log.info("[MINIO_DELETE_BEGIN]");
		log.info("[MINIO_DELETE] - {}", url);

		if (url.equalsIgnoreCase("default")) {
			log.info("[MINIO_DELETE_SKIP_DEFAULT_IMAGE]");
			log.info("[MINIO_DELETE_END]");
			return true;
		}

		log.info("[MINIO_DELETE_OBJECT_KEY] - [{}]]", url.split(String.format("/%s/", bucket))[1]);
		String objectKey = url.split(String.format("/%s/", bucket))[1];
		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucket)
			.object(objectKey)
			.build();
		try {
			minioClient.removeObject(removeObjectArgs);
			log.info("[MINIO_DELETE_END]");
		} catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException | ArrayIndexOutOfBoundsException e) {
			log.error("[MINIO_DELETE_ERROR] URL: [{}]", url);
			log.error("[MINIO_DELETE_ERROR] MESSAGE : [{}]", e.getMessage(), e);
		}
		return true;
	}

	@Override
	public File getFile(String url) {
		return null;
	}
}
