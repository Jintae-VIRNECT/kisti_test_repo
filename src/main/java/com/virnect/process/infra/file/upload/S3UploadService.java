package com.virnect.process.infra.file.upload;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: base
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"staging", "production", "test"})
@Component
@RequiredArgsConstructor
public class S3UploadService implements FileUploadService {
	private static String CONTENT_DIRECTORY = "contents";
	private static String REPORT_DIRECTORY = "report";
	private static String REPORT_FILE_EXTENSION = ".jpg";

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	private final AmazonS3 amazonS3Client;

	@Override
	public void delete(String url) {
		String prefix = "https://" + bucketName + ".s3." + amazonS3Client.getRegionName() + ".amazonaws.com/";
		if (!StringUtils.hasText(url) || url.contains("virnect_target.png")) {
			log.info("[S3 DELETE] DEFAULT FILE SKIP. URL : {}", url);
		}
		String[] fileSplit = url.split(prefix);
		String key = fileSplit[fileSplit.length - 1];

		log.info("[S3 FILE DELETE] DELETE REQUEST. BUCKET : {}, KEY : {}", bucketName, key);

		amazonS3Client.deleteObject(bucketName, key);
		log.info(FilenameUtils.getName(url) + " 파일이 AWS S3(" + bucketName + "/" + key + ")에서 삭제되었습니다.");
	}

	@Override
	public String base64ImageUpload(String base64Image, String workspaceUUID) {

		byte[] image = Base64.getDecoder().decode(base64Image);
		log.info("[S3 BASE64 UPLOAD] UPLOAD FILE SIZE : {} (byte)", image.length);
		String randomFileName = String.format(
			"%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
			REPORT_FILE_EXTENSION
		);

		String objectName = String.format("workspace/%s/%s", REPORT_DIRECTORY, randomFileName);
		if (StringUtils.hasText(workspaceUUID)) {
			objectName = String.format("workspace/%s/%s/%s", workspaceUUID, REPORT_DIRECTORY, randomFileName);
		}

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		objectMetadata.setContentLength(image.length);
		objectMetadata.setHeader("filename", randomFileName);
		objectMetadata.setContentDisposition(String.format("attachment; filename=\"%s\"", randomFileName));

		PutObjectRequest putObjectRequest = new PutObjectRequest(
			bucketName, objectName, new ByteArrayInputStream(image), objectMetadata).withCannedAcl(
			CannedAccessControlList.PublicRead);

		log.info(
			"[S3 BASE64 UPLOAD] UPLOAD REQUEST. BUCKET : {}, KEY : {}, CONTENT TYPE : {}", bucketName,
			objectName,
			MediaType.APPLICATION_OCTET_STREAM_VALUE
		);
		try {
			amazonS3Client.putObject(putObjectRequest);
			log.info("[S3 BASE64 UPLOAD] UPLOAD SUCCESS.");
			String url = amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
			log.info("[S3 BASE64 UPLOAD] UPLOADED URL : {}", url);
			return url;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
		}
	}

	@Override
	public String getDefaultReportImagePath(String fileName) {
		String objectName = "workspace/report/" + fileName;
		log.info("[S3 GET FILE PATH] BUCKET : {}, KEY : {} ", bucketName, objectName);
		return amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
	}

}
