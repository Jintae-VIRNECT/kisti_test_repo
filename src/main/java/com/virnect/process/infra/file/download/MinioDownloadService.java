package com.virnect.process.infra.file.download;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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

import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-02-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"local", "develop", "onpremise"})
@Component
@RequiredArgsConstructor
public class MinioDownloadService implements FileDownloadService {
	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucketName;

	@Value("${file.prefix}")
	private String prefix;

	@Override
	public byte[] fileDownloadByFileName(String fileUrl) {
		log.info("PARSER - URL: [{}]", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		log.info("PARSER - KEY: [{}]", objectName);
		GetObjectArgs getObjectArgs = GetObjectArgs.builder()
			.bucket(bucketName)
			.object(objectName)
			.build();
		try (InputStream inputStream = minioClient.getObject(getObjectArgs)) {
			return IOUtils.toByteArray(inputStream);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException exception) {
			log.error(exception.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DOWNLOAD);
		}
	}
}
