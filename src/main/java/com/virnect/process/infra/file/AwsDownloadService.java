package com.virnect.process.infra.file;

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
public class AwsDownloadService implements FileDownloadService {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Value("${cloud.aws.s3.bucket.resource}")
	private String bucketResource;

	@Override
	public byte[] fileDownloadByFileName(String fileName) {
		String resourcePath = fileName.split(bucketResource)[1];
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, bucketResource + resourcePath);
		try (S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
			 S3ObjectInputStream objectInputStream = s3Object.getObjectContent()) {
			return IOUtils.toByteArray(objectInputStream, s3Object.getObjectMetadata().getContentLength());
		} catch (IOException e) {
			log.error("Error Message:     {}", e.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DOWNLOAD);
		}
	}
}
