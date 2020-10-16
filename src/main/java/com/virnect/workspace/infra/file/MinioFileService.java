package com.virnect.workspace.infra.file;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
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
@Profile({"local", "develop", "onpremise"})
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {
	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucket;

	@Value("${minio.resource}")
	private String resource;

	@Value("${minio.extension}")
	private String allowExtension;

	@Override
	public String upload(MultipartFile file) throws IOException {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

		if (!allowExtension.contains(extension)) {
			//log.error("Not Allow File Extension. Request File Extension >> {}", extension);
			//throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}

		String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
		String objectName = resource + "/" + uniqueFileName;

		PutObjectArgs putObjectArgs = PutObjectArgs.builder()
			.bucket(bucket)
			.object(objectName)
			.contentType(file.getContentType())
			.stream(file.getInputStream(), file.getSize(), -1)
			.build();
		log.info("Upload File Info >> bucket : {}, resource : {}, filename : {}, fileSize(byte) : {}", bucket, resource,
			uniqueFileName, file.getSize()
		);
		try {

			ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
			log.info("Minio file upload response : [{}]", response);

			return minioClient.getObjectUrl(bucket, objectName);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
			ServerException | XmlParserException exception) {
			log.error(exception.getClass().toString());
			log.error(exception.getMessage());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}

	}

	@Override
	public void delete(String fileUrl) {
		String fileName = FilenameUtils.getName(fileUrl);
		String objectName = resource + "/" + fileName;
		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucket)
			.object(objectName)
			.build();
		log.info("Delete File Info >> bucket : {}, resource : {}, filename : {}", bucket, resource, fileName);
		try {
			minioClient.removeObject(removeObjectArgs);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {
			log.error(exception.getMessage());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
	}

	@Override
	public String getFileUrl(String fileName) {
		String objectName = resource + "/" + fileName;
		try {
			return minioClient.getObjectUrl(bucket, objectName);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {

			log.error(exception.getMessage());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
	}
}

