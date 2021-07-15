package com.virnect.message.infra.file;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.message.exception.MessageException;
import com.virnect.message.global.error.ErrorCode;

/**
 * Project: PF-Workspace
 * DATE: 2020-09-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"local", "develop"})
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioFileService implements FileService {
	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucketName;

	@Override
	public byte[] getObjectBytes(String objectName) {
		try {
			log.info("[GET FILE OBJECT] bucket name : [{}], request object name : [{}]", bucketName, objectName);
			GetObjectArgs getObjectArgs = GetObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.build();
			InputStream inputStream = minioClient.getObject(getObjectArgs);
			return IOUtils.toByteArray(inputStream);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {
			log.error(exception.getMessage());
			log.error(exception.getClass().toString());
			throw new MessageException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
	}

}