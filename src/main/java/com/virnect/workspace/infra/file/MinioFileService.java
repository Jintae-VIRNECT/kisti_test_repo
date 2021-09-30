package com.virnect.workspace.infra.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.RegionConflictException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;

/**
 * Project: PF-Workspace
 * DATE: 2020-09-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"local", "develop", "onpremise", "test"})
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {
	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucket;

	@Value("${minio.prefix}")
	private String prefix;

	@Value("${minio.extension}")
	private String allowExtension;

	@Override
	public String upload(MultipartFile file, String workspaceUUID) throws IOException {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
		String objectName = String.format("workspace/%s/profile/%s", workspaceUUID, uniqueFileName);

		PutObjectArgs putObjectArgs = PutObjectArgs.builder()
			.bucket(bucket)
			.object(objectName)
			.contentType(file.getContentType())
			.stream(file.getInputStream(), file.getSize(), -1)
			.build();
		log.info("[FILE UPLOAD] Upload File Info >> bucket : {}, object : {}, fileSize(byte) : {}",
			bucket, objectName,
			file.getSize()
		);
		try {
			bucketExistCheck();
			ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
			String uploadPath = minioClient.getObjectUrl(bucket, objectName);
			log.info("[FILE UPLOAD] Upload Result path : [{}],", uploadPath);
			return uploadPath;
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
			ServerException | XmlParserException exception) {
			log.error(exception.getClass().toString());
			log.error(exception.getMessage());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}

	}

	@Override
	public void delete(String fileUrl) {
		if (!StringUtils.hasText(fileUrl)) {
			return;
		}
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];

		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucket)
			.object(objectName)
			.build();

		try {
			if (fileUrl.contains(DefaultFile.WORKSPACE_PROFILE_IMG.getFileName())) {
				log.info(
					"[FILE DELETE] Not Delete Default File Info >> bucket : {}, object : {}", bucket, objectName
				);
			} else {
				minioClient.removeObject(removeObjectArgs);
				log.info(
					"[FILE DELETE] Delete File Info >> bucket : {}, object : {}", bucket, objectName
				);
			}

		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {
			log.error(exception.getMessage());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
	}

	@Override
	public String getDefaultFileUrl(String fileName) {
		String objectName = String.format("workspace/%s", fileName);
		log.info("[FILE FIND] get file url request. file name : {}", fileName);
		//1. 버킷 확인
		bucketExistCheck();

		//2. object 존재 확인
		boolean objectExist = objectExistCheck(objectName);

		//3. metadata 경로에 있는 default 파일의 경우 없으면 올린다.
		File file = new File("metadata/" + fileName);

		if (!objectExist && !file.exists()) {
			throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_FILE);
		}

		if (!objectExist && file.exists()) {
			MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
			String mimeType = mimeTypesMap.getContentType(file);
			try (InputStream inputStream = new FileInputStream(file)) {
				long fileSize = file.length();
				PutObjectArgs putObjectArgs = PutObjectArgs.builder()
					.bucket(bucket)
					.object(objectName)
					.contentType(mimeType)
					.stream(inputStream, file.length(), -1)
					.build();
				log.info(
					"[FILE FIND] Default file upload Request >> bucket : {}, object : {}, size : {}, content-type : {}",
					bucket, objectName, fileSize, mimeType
				);
				minioClient.putObject(putObjectArgs);
				return getObjectUrl(bucket, objectName);
			} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
				NoSuchAlgorithmException | ServerException | XmlParserException e) {
				log.error(e.getMessage());
				log.error(e.getClass().toString());
				throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
			}
		}
		return getObjectUrl(bucket, objectName);
	}

	private String getObjectUrl(String bucket, String objectName) {
		try {
			return minioClient.getObjectUrl(bucket, objectName);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {
			log.error(exception.getMessage());
			log.error(exception.getClass().toString());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}

	}

	private boolean objectExistCheck(String objectName) {
		try {
			StatObjectArgs statObjectArgs = StatObjectArgs.builder()
				.bucket(bucket)
				.object(objectName)
				.build();
			ObjectStat objectStat = minioClient.statObject(statObjectArgs);
			log.info(
				"[FILE FIND] Find file Info >> bucket : {}, object : {}, size : {}, content-type : {}", bucket,
				objectName, objectStat.length(), objectStat.contentType()
			);
			return true;
		} catch (ErrorResponseException | IllegalArgumentException | InsufficientDataException |
			InternalException | InvalidBucketNameException | InvalidKeyException |
			InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
			XmlParserException e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	private void bucketExistCheck() {
		try {
			BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucket).build();
			boolean bucketExists = minioClient.bucketExists(bucketExistsArgs);
			if (!bucketExists) {
				MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
					.objectLock(false)
					.bucket(bucket)
					.build();
				minioClient.makeBucket(makeBucketArgs);
				log.info(
					"[BUCKET CREATE] Create Bucket success Info >> bucket : {}, objectLock >> {}",
					makeBucketArgs.bucket(),
					makeBucketArgs.objectLock()
				);
			}
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | RegionConflictException e) {
			log.error(
				"[BUCKET CREATE] Create Bucket fail Info >> bucket : {}", bucket
			);
			log.error(e.getMessage());
			log.error(e.getClass().toString());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);

		}

	}

}