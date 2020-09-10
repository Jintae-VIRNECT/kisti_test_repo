package com.virnect.download.infra.file.donwload;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
public class S3FileDownloadService implements FileDownloadService {

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Value("${file.upload-path}")
	private String fileUploadPath;

	private AmazonS3 amazonS3Client;

	public S3FileDownloadService(AmazonS3 amazonS3) {
		this.amazonS3Client = amazonS3;
	}

	public byte[] fileDownload(String fileName) throws IOException {
		S3Object object = amazonS3Client.getObject(bucketName, fileUploadPath + fileName);
		S3ObjectInputStream inputStream = object.getObjectContent();
		return IOUtils.toByteArray(inputStream, object.getObjectMetadata().getContentLength());
	}
}
