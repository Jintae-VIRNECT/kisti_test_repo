package com.virnect.content.infra.file.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: base
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"staging", "production"})
@Component
@RequiredArgsConstructor
public class S3UploadService implements FileUploadService {
	private final AmazonS3 amazonS3Client;
	private static final String PROJECT_DIRECTORY = "project";
	private static final String CONTENT_DIRECTORY = "content";
	private static final String REPORT_DIRECTORY = "report";
	private static final String REPORT_FILE_EXTENSION = ".png";
	private static final String V_TARGET_DEFAULT_NAME = "virnect_target.png";

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Value("${file.prefix}")
	private String prefix;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	@Override
	public void deleteByFileUrl(String fileUrl) {
		if (!StringUtils.hasText(fileUrl)) {
			return;
		}
		log.info("[AWS S3 FILE DELETE] DELETE BEGIN. URL : {}", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];

		if (fileUrl.contains(V_TARGET_DEFAULT_NAME)) {
			log.info("[MINIO FILE DELETE] DEFAULT FILE SKIP. KEY : {}", objectName);
			return;
		}
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectName);
		log.info("[AWS S3 FILE DELETE] DELETE REQUEST. BUCKET : {}, KEY : {}", bucketName, objectName);
		try {
			amazonS3Client.deleteObject(deleteObjectRequest);
			log.info("[AWS S3 FILE DELETE] DELETE SUCCESS.");
		} catch (SdkClientException e) {
			log.info("[AWS S3 FILE DELETE] DELETE FAIL. ERROR MESSAGE : {}", e.getMessage());
			//throw new ContentServiceException(ErrorCode.ERR_DELETE_CONTENT);
		}
	}

	@Override
	public String uploadByBase64Image(
		String base64Image, String fileDir, String workspaceUUID, String fileName
	) {
		log.info("[AWS S3 BASE64 UPLOAD] UPLOAD BEGIN. DIR : {}, NAME : {}", fileDir, fileName);
		byte[] image = Base64.getDecoder().decode(base64Image);
		log.info("[AWS S3 BASE64 UPLOAD] UPLOAD FILE SIZE : {} (byte)", image.length);
		if (image.length <= 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		objectMetadata.setContentLength(image.length);
		objectMetadata.setHeader("filename", fileName);
		objectMetadata.setContentDisposition(String.format("attachment; filename=\"%s\"", fileName));

		String objectName = String.format("workspace/%s/%s/%s", workspaceUUID, fileDir, fileName);
		PutObjectRequest putObjectRequest = new PutObjectRequest(
			bucketName, objectName, new ByteArrayInputStream(image), objectMetadata);
		putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
		log.info(
			"[AWS S3 BASE64 UPLOAD] UPLOAD REQUEST. BUCKET : {}, KEY : {}, CONTENT TYPE : {}", bucketName, objectName,
			MediaType.APPLICATION_OCTET_STREAM_VALUE
		);
		amazonS3Client.putObject(putObjectRequest);
		log.info("[AWS S3 BASE64 UPLOAD] UPLOAD SUCCESS.");
		String url = amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
		log.info("[AWS S3 BASE64 UPLOAD] UPLOADED URL : {}", url);
		return url;
	}

	@Override
	public String uploadByFileInputStream(
		MultipartFile file, String fileDir, String workspaceUUID, String fileNameWithoutExtension
	) {
		log.info("[AWS S3 FILE UPLOAD] UPLOAD BEGIN. DIR : {}, NAME : {}", fileDir, fileNameWithoutExtension);

		// 1. 파일 크기 확인
		log.info("[AWS S3 FILE UPLOAD] UPLOAD FILE SIZE : {} (byte)", file.getSize());
		if (file.getSize() <= 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}

		// 2. 파일 확장자 확인
		String fileExtension = String.format(
			".%s", Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));

		if (fileDir.equals(CONTENT_DIRECTORY) || fileDir.equals(PROJECT_DIRECTORY)) {
			if (!allowedExtension.contains(fileExtension)) {
				log.info("[AWS S3 FILE UPLOAD] UNSUPPORTED FILE. NAME : {}", file.getOriginalFilename());
				throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
			}
		}

		// 3. 파일 메타데이터 생성
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setHeader("filename", fileNameWithoutExtension + fileExtension);
		objectMetadata.setContentDisposition(
			String.format("attachment; filename=\"%s\"", fileNameWithoutExtension + fileExtension));

		String objectName = String.format("workspace/%s/%s%s", fileDir, fileNameWithoutExtension, fileExtension);
		if (StringUtils.hasText(workspaceUUID)) {
			objectName = String.format(
				"workspace/%s/%s/%s%s", workspaceUUID, fileDir, fileNameWithoutExtension, fileExtension);
		}
		// 4. 스트림으로 aws s3에 업로드
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
				bucketName, objectName, file.getInputStream(), objectMetadata).withCannedAcl(
				CannedAccessControlList.PublicRead);
			log.info(
				"[AWS S3 FILE UPLOAD] UPLOAD REQUEST. BUCKET : {}, KEY : {}, CONTENT TYPE : {}", bucketName, objectName,
				file.getContentType()
			);
			amazonS3Client.putObject(putObjectRequest);
			log.info("[AWS S3 FILE UPLOAD] UPLOAD SUCCESS.");
			String url = amazonS3Client.getUrl(bucketName, objectName)
				.toExternalForm();
			log.info("[AWS S3 FILE UPLOAD] UPLOADED URL : {}", url);
			return url;
		} catch (AmazonServiceException e) {
			log.error("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			log.error("Error Message:     {}", e.getMessage());
			log.error("HTTP Status Code:  {}", e.getStatusCode());
			log.error("AWS Error Code:    {}", e.getErrorCode());
			log.error("Error Type:        {}", e.getErrorType());
			log.error("Request ID:        {}", e.getRequestId());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		} catch (IOException e) {
			log.error("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			log.error("Error Message:     {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}

	// 로컬 임시 파일 삭제
	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

	// 이미지 전송 요청을 받아 로컬 파일로 변환
	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}

		return Optional.empty();
	}

	/**
	 * AWS S3 이미지 업로드 요청 전송
	 *
	 * @param uploadFile - 업로드 대상 파일
	 * @param fileName   - 파일 이름
	 * @return 이미지 URL
	 */
	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
			new PutObjectRequest(bucketName, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucketName, fileName).toString();
	}

	@Override
	public String copyByFileObject(
		String sourceFileUrl, String destinationFileDir,
		String destinationWorkspaceUUID, String destinationFileNameWithoutExtension
	) {
		log.info(
			"[MINIO FILE COPY] COPY BEGIN. URL : {}, DESTINATION DIR : {}, DESTINATION NAME : {}", sourceFileUrl,
			destinationFileDir, destinationFileNameWithoutExtension
		);
		String[] fileSplit = sourceFileUrl.split("/");
		String sourceFileDir = fileSplit[fileSplit.length - 2];//contents
		String sourceFileName = fileSplit[fileSplit.length - 1]; //UUID
		String sourceFileExtension = sourceFileUrl.substring(sourceFileUrl.lastIndexOf(".") + 1); //Ares

		String sourceObjectName = String.format("workspace/%s/%s", sourceFileDir, sourceFileName);
		String destinationObjectName = String.format(
			"workspace/%s/%s/%s.%s", destinationWorkspaceUUID, destinationFileDir, destinationFileNameWithoutExtension,
			sourceFileExtension
		);

		CopyObjectRequest copyObjRequest = new CopyObjectRequest(
			bucketName, sourceObjectName, bucketName, destinationObjectName);
		log.info("[MINIO FILE COPY] COPY REQUEST. BUCKET : {}, SOURCE KEY : {}, DESTINATION KEY : {}", bucketName,
			sourceObjectName, destinationObjectName
		);

		amazonS3Client.copyObject(copyObjRequest);
		log.info("[MINIO FILE COPY] COPY SUCCESS.");
		String url = amazonS3Client.getUrl(bucketName, destinationObjectName).toString();
		log.info("[MINIO FILE COPY] COPIED URL : {}", url);
		return url;
	}
}
