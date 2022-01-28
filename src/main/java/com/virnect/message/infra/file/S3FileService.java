package com.virnect.message.infra.file;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"staging", "production"})
@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileService implements FileService {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Override
	public byte[] getObjectBytes(String objectName) throws IOException {
		log.info("[GET FILE OBJECT] bucket name : [{}], request object name : [{}]", bucketName, objectName);
		S3Object object = amazonS3Client.getObject(bucketName, objectName);
		S3ObjectInputStream inputStream = object.getObjectContent();
		return IOUtils.toByteArray(inputStream, object.getObjectMetadata().getContentLength());
	}
}
