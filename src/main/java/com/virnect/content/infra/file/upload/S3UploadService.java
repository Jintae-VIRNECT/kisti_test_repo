package com.virnect.content.infra.file.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
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

	private static final String CONTENT_DIRECTORY = "contents";
	private static final String REPORT_DIRECTORY = "report";
	private static final String REPORT_FILE_EXTENSION = ".png";
	private static final String VTARGET_FILE_NAME = "virnect_target.png";

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;
	@Value("${cloud.aws.s3.bucket.resource}")
	private String bucketResource;
	@Value("#{'${upload.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	private String upload(MultipartFile file, String fileName) throws IOException {
		log.info("[AWS S3 UPLOADER] - UPLOAD BEGIN");
		if (file.getSize() <= 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
		String fileExtension = String.format(
			".%s", Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));

		if (!allowedExtension.contains(fileExtension)) {
			log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
			throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
		}

		/*
		File uploadFile = convert(file)
			.orElseThrow(() -> {
				log.info("MultipartFile -> File 변환 실패");
				return new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
			});

		String saveFileName = String.format("%s%s/%s%s", bucketResource, CONTENT_DIRECTORY, fileName, fileExtension);
		String uploadFileUrl = putS3(uploadFile, saveFileName);
		// S3에서 다운로드 받은 파일
		File downlodedFile = new File("upload/" + uploadFile.getName());

		removeNewFile(uploadFile);
		removeNewFile(downlodedFile);

		return uploadFileUrl;
*/
		String saveFileName = String.format("%s%s/%s%s", bucketResource, CONTENT_DIRECTORY, fileName, fileExtension);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		amazonS3Client.putObject(
			new PutObjectRequest(bucketName, saveFileName, file.getInputStream(), metadata).withCannedAcl(
				CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucketName, saveFileName).toString();

	}

	@Override
	public boolean delete(String url) {
		if (url.equals("default") || FilenameUtils.getName(url).equals(VTARGET_FILE_NAME)) {
			log.info("기본 이미지는 삭제하지 않습니다. FILE PATH >> [{}]", url);
		} else {
			//String resourceEndPoint = String.format("%s%s", bucketResource, CONTENT_DIRECTORY);
			//            String resourceEndPoint = String.format("%s/%s", bucketName, bucketResource);
			//String key = url.split(String.format("/%s/%s", bucketResource, CONTENT_DIRECTORY))[1];
			String key = bucketResource + CONTENT_DIRECTORY + "/" + FilenameUtils.getName(url);
			amazonS3Client.deleteObject(bucketName, key);
			//amazonS3Client.deleteObject(resourceEndPoint, key);

			log.info(FilenameUtils.getName(url) + " 파일이 AWS S3(" + bucketName + "/" + key + ")에서 삭제되었습니다.");
		}
		return true;
	}

	@Override
	public String base64ImageUpload(String base64Image) {
		byte[] image = Base64.getDecoder().decode(base64Image);
		String randomFileName = String.format(
			"%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
			REPORT_FILE_EXTENSION
		);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		objectMetadata.setContentLength(image.length);
		objectMetadata.setHeader("filename", randomFileName);
		objectMetadata.setContentDisposition(String.format("attachment; filename=\"%s\"", randomFileName));

		String s3FileKey = String.format("%s%s/%s", bucketResource, REPORT_DIRECTORY, randomFileName);
		PutObjectRequest putObjectRequest = new PutObjectRequest(
			bucketName, s3FileKey, new ByteArrayInputStream(image), objectMetadata);
		putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
		amazonS3Client.putObject(putObjectRequest);
		log.info("[AWS S3 FILE INPUT STREAM UPLOADER] - UPLOAD END");
		String url = amazonS3Client.getUrl(bucketName, s3FileKey)
			.toExternalForm();
		log.info("[AWS S3 RESOURCE URL: {}]", url);
		log.info("[AWS CDN URL: {}]", s3FileKey);
		return url;

        /*File convertImage = new File(randomFileName);
        try (FileOutputStream fos = new FileOutputStream(convertImage)) {
            fos.write(image);
            String saveFileName = String.format("%s%s/%s", bucketResource, REPORT_DIRECTORY, randomFileName);
            String uploadFileUrl = putS3(convertImage, saveFileName);
            removeNewFile(convertImage);
            return uploadFileUrl;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }*/
	}

	/**
	 * multipart file 을 input stream 으로 s3에 업로드
	 *
	 * @param file     - 업로드하고자하는 MultipartFile
	 * @param fileName - 저장하고자 하는 파일 명
	 * @return - s3 파일 url
	 * @throws IOException
	 */
	@Override
	public String uploadByFileInputStream(MultipartFile file, String fileName) throws IOException {
		log.info("[AWS S3 FILE INPUT STREAM UPLOADER] - UPLOAD BEGIN");

		// 1. 파일 크기 확인
		log.info("[AWS S3 FILE INPUT STREAM UPLOADER] - UPLOAD FILE SIZE >> " + file.getSize());
		if (file.getSize() <= 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}

		// 2. 파일 확장자 확인
		String fileExtension = String.format(
			".%s", Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));

		if (!allowedExtension.contains(fileExtension)) {
			log.error("[AWS S3 FILE INPUT STREAM UPLOADER] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
			throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
		}

		// 3. 파일 메타데이터 생성
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setHeader("filename", fileName + fileExtension);
		objectMetadata.setContentDisposition(String.format("attachment; filename=\"%s\"", fileName + fileExtension));

		// 4. 스트림으로 aws s3에 업로드
		try {
			String s3FileKey = String.format("%s%s/%s%s", bucketResource, CONTENT_DIRECTORY, fileName, fileExtension);

			PutObjectRequest putObjectRequest = new PutObjectRequest(
				bucketName, s3FileKey, file.getInputStream(), objectMetadata
			).withCannedAcl(CannedAccessControlList.PublicRead);
			amazonS3Client.putObject(putObjectRequest);
			log.info("[AWS S3 FILE INPUT STREAM UPLOADER] - UPLOAD END");
			String url = amazonS3Client.getUrl(bucketName, s3FileKey)
				.toExternalForm();
			log.info("[AWS S3 RESOURCE URL: {}]", url);
			log.info("[AWS CDN URL: {}]", s3FileKey);
			return url;
		} catch (AmazonServiceException e) {
			log.error("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			log.error("Error Message:     {}", e.getMessage());
			log.error("HTTP Status Code:  {}", e.getStatusCode());
			log.error("AWS Error Code:    {}", e.getErrorCode());
			log.error("Error Type:        {}", e.getErrorType());
			log.error("Request ID:        {}", e.getRequestId());
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
	public String copyByFileObject(String sourceFileName, String destinationFileName) {
		String sourceObjectName = String.format("%s%s/%s", bucketResource, CONTENT_DIRECTORY, sourceFileName);
		String destinationObjectName = String.format("%s%s/%s", bucketResource, CONTENT_DIRECTORY, destinationFileName);
		log.info("[COPY FILE REQUEST] SOURCE : {}, DESTINATION : {}", sourceObjectName, destinationObjectName);

		CopyObjectRequest copyObjRequest = new CopyObjectRequest(
			bucketName, sourceObjectName, bucketName, destinationObjectName);
		amazonS3Client.copyObject(copyObjRequest);

		return amazonS3Client.getUrl(bucketName, destinationObjectName).toString();
	}
}
