package com.virnect.uaa.infra.file;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
@Profile(value = {"dev", "local", "develop", "onpremise","test"})
@RequiredArgsConstructor
public class MinioFileService implements FileService {
	private static final String URL_SEPARATOR = "/";
	private static final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList(".jpg", ".png", ".JPG", ".PNG");
	private static final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;

	private final MinioClient minioClient;
	@Value("${minio.bucket:virnect-platform}")
	private String bucketName;
	@Value("${minio.resource:user/profile}")
	private String bucketResource;

	@Override
	public String upload(MultipartFile file) throws IOException {

		// 1. 빈 파일 여부 확인
		if (file.getSize() == 0) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
		}

		// 2. 확장자 검사
		String fileExtension = getExtensionFromFileName(file.getOriginalFilename());
		if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
			log.error("[UNSUPPORTED_EXTENSION_FILE] [{}]", file.getOriginalFilename());
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_EXTENSION);
		}

		// 3. 파일 용량 검사
		if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_SIZE_LIMIT);
		}

		String fileName = bucketResource + URL_SEPARATOR + generateUniqueFileName(fileExtension);

		log.info("BUCKET: [{}], FILENAME: [{}]", bucketName, fileName);

		PutObjectArgs putObjectArgs = PutObjectArgs.builder()
			.contentType(file.getContentType())
			.bucket(bucketName)
			.object(fileName)
			.stream(file.getInputStream(), file.getInputStream().available(), -1)
			.build();
		try {
			minioClient.putObject(putObjectArgs);
			String url = minioClient.getObjectUrl(bucketName, fileName);
			log.info("[IMAGE_URL] - [{}]", url);
			return url;
		} catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
			log.error("[UPLOAD_ERROR] - {}", e.getMessage(), e);
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
		}
	}

	@Override
	public String upload(MultipartFile file, String fileName) throws IOException {
		return null;
	}

	@Override
	public boolean delete(String url) {
		if (Default.USER_PROFILE.isValueEquals(url)) {
			log.info("기본 이미지는 삭제하지 않습니다.");
			return false;
		}

		String key = bucketResource + URL_SEPARATOR + url.substring(url.lastIndexOf(URL_SEPARATOR) + 1);

		log.info("MinIO(" + bucketName + ")에서 (" + key + ")가 삭제되었습니다.");

		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucketName)
			.object(key)
			.build();
		try {
			minioClient.removeObject(removeObjectArgs);
		} catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException | ArrayIndexOutOfBoundsException e) {
			log.error("[DELETE_ERROR] URL: [{}]", url);
			log.error("[DELETE_ERROR] MESSAGE : [{}]", e.getMessage(), e);
		}
		return true;
	}

	@Override
	public File getFile(String url) {
		return null;
	}
}
