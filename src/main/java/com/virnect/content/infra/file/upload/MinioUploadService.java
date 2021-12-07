package com.virnect.content.infra.file.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"local", "develop", "onpremise", "test"})
@Component
@RequiredArgsConstructor
public class MinioUploadService implements FileUploadService {
	private final MinioClient minioClient;
	private static final String PROJECT_DIRECTORY = "project";
	private static final String CONTENT_DIRECTORY = "content";
	private static final String REPORT_DIRECTORY = "report";
	private static final String V_TARGET_DEFAULT_NAME = "virnect_target.png";

	@Value("${minio.bucket}")
	private String bucketName;

	@Value("${minio.server}")
	private String minioServer;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	@Override
	public void deleteByFileUrl(String fileUrl) {
		String prefix = minioServer + "/" + bucketName + "/";
		log.info("[MINIO FILE DELETE] DELETE BEGIN. URL : {}", fileUrl);
		if (!StringUtils.hasText(fileUrl)) {
			return;
		}
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];

		if (fileUrl.contains(V_TARGET_DEFAULT_NAME)) {
			log.info("[MINIO FILE DELETE] DEFAULT FILE SKIP. KEY : {}", objectName);
			return;
		}

		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucketName)
			.object(objectName)
			.build();
		log.info("[MINIO FILE DELETE] DELETE REQUEST. BUCKET : {}, KEY : {}", bucketName, objectName);
		CompletableFuture.runAsync(() -> {
			try {
				minioClient.removeObject(removeObjectArgs);
				log.info("[MINIO FILE DELETE] DELETE SUCCESS.");
			} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException |
				NoSuchAlgorithmException | ServerException | XmlParserException e) {
				log.error("[MINIO FILE DELETE] DELETE FAIL. ERROR MESSAGE : {}", e.getMessage());
			}
		});

	}

	@Override
	public String uploadByBase64Image(
		String base64Image, String fileDir, String workspaceUUID, String fileName
	) {
		log.info("[MINIO BASE64 UPLOAD] UPLOAD BEGIN. DIR : {}, NAME : {}", fileDir, fileName);
		byte[] image = Base64.getDecoder().decode(base64Image);
		log.info("[MINIO BASE64 UPLOAD] UPLOAD FILE SIZE : {} (byte)", image.length);
		if (image.length <= 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
		String objectName = String.format("workspace/%s/%s/%s", workspaceUUID, fileDir, fileName);

		try (InputStream inputStream = new ByteArrayInputStream(image)) {
			PutObjectArgs putObjectArgs = PutObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.stream(inputStream, image.length, -1)
				.build();
			log.info(
				"[MINIO BASE64 UPLOAD] UPLOAD REQUEST. BUCKET : {}, KEY : {}, CONTENT TYPE : {}", bucketName,
				objectName,
				MediaType.APPLICATION_OCTET_STREAM_VALUE
			);
			minioClient.putObject(putObjectArgs);
			log.info("[MINIO BASE64 UPLOAD] UPLOAD SUCCESS.");
			String url = minioServer + "/" + bucketName + "/" + objectName;
			log.info("[MINIO BASE64 UPLOAD] UPLOADED URL : {}", url);
			return url;
		} catch (InvalidKeyException | NoSuchAlgorithmException | ErrorResponseException | InvalidResponseException | ServerException | InsufficientDataException | XmlParserException | InternalException | IOException e) {
			log.error("[MINIO BASE64 UPLOAD] UPLOAD FILE. ERROR MESSAGE : {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}

	@Override
	public String uploadByFileInputStream(
		MultipartFile file, String fileDir, String workspaceUUID, String fileNameWithoutExtension
	) {
		log.info("[MINIO FILE UPLOAD] UPLOAD BEGIN. DIR : {}, NAME : {}", fileDir, fileNameWithoutExtension);

		// 1. 파일 크기 확인
		log.info("[MINIO FILE UPLOAD] UPLOAD FILE SIZE : {} (byte)", file.getSize());
		if (file.getSize() <= 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}

		// 2. 파일 확장자 확인
		String fileExtension = String.format(
			".%s", Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));
		if (fileDir.equals(CONTENT_DIRECTORY) || fileDir.equals(PROJECT_DIRECTORY)) {
			if (!allowedExtension.contains(fileExtension)) {
				log.info("[MINIO FILE UPLOAD] UNSUPPORTED FILE. NAME : {}", file.getOriginalFilename());
				throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
			}
		}
		String objectName = String.format("workspace/%s/%s%s", fileDir, fileNameWithoutExtension, fileExtension);
		if (StringUtils.hasText(workspaceUUID)) {
			objectName = String.format(
				"workspace/%s/%s/%s%s", workspaceUUID, fileDir, fileNameWithoutExtension, fileExtension);
		}

		try {
			PutObjectArgs putObjectArgs = PutObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.contentType(file.getContentType())
				.stream(file.getInputStream(), file.getSize(), -1)
				.build();
			log.info(
				"[MINIO FILE UPLOAD] UPLOAD REQUEST. BUCKET : {}, KEY : {}, CONTENT TYPE : {}", bucketName, objectName,
				file.getContentType()
			);
			minioClient.putObject(putObjectArgs);
			log.info("[MINIO FILE UPLOAD] UPLOAD SUCCESS.");
			String url = minioServer + "/" + bucketName + "/" + objectName;
			log.info("[MINIO FILE UPLOAD] UPLOADED URL : {}", url);
			return url;
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException e) {
			log.error("[MINIO FILE UPLOAD] UPLOAD FAIL. ERROR MESSAGE : {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}

	@Override
	public String copyByFileObject(
		String sourceFileUrl, String destinationFileDir,
		String destinationWorkspaceUUID, String destinationFileNameWithoutExtension
	) {
		String prefix = minioServer + "/" + bucketName + "/";
		log.info(
			"[MINIO FILE COPY] COPY BEGIN. URL : {}, DESTINATION DIR : {}, DESTINATION NAME : {}", sourceFileUrl,
			destinationFileDir, destinationFileNameWithoutExtension
		);
		String[] fileSplit = sourceFileUrl.split(prefix);
		String sourceObjectName = fileSplit[fileSplit.length - 1];
		String sourceFileExtension = sourceFileUrl.substring(sourceFileUrl.lastIndexOf(".") + 1); //Ares
		String destinationObjectName = String.format(
			"workspace/%s/%s/%s.%s", destinationWorkspaceUUID, destinationFileDir, destinationFileNameWithoutExtension,
			sourceFileExtension
		);

		CopySource copySource = CopySource.builder()
			.bucket(bucketName)
			.object(sourceObjectName)
			.build();
		CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
			.bucket(bucketName)
			.object(destinationObjectName)
			.source(copySource)
			.build();
		log.info("[MINIO FILE COPY] COPY REQUEST. BUCKET : {}, SOURCE KEY : {}, DESTINATION KEY : {}", bucketName,
			sourceObjectName, destinationObjectName
		);
		try {
			minioClient.copyObject(copyObjectArgs);
			log.info("[MINIO FILE COPY] COPY SUCCESS.");
			String url = minioServer + "/" + bucketName + "/" + destinationObjectName;
			log.info("[MINIO FILE COPY] COPIED URL : {}", url);
			return url;
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException exception) {
			log.error(exception.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}
}
