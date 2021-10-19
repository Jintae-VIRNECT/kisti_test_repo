package com.virnect.process.infra.file.download;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

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
@RequiredArgsConstructor
@Profile({"staging", "production", "test"})
@Component
@Slf4j
public class S3DownloadService implements FileDownloadService {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Value("${file.prefix}")
	private String prefix;

	@Override
	public byte[] fileDownloadByFileName(String fileUrl) {
		log.info("PARSER - URL: [{}]", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		log.info("PARSER - KEY: [{}]", objectName);

		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
		try (S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
			 S3ObjectInputStream objectInputStream = s3Object.getObjectContent()) {
			return IOUtils.toByteArray(objectInputStream, s3Object.getObjectMetadata().getContentLength());
		} catch (IOException e) {
			log.error("Error Message:     {}", e.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DOWNLOAD);
		}
	}
}
