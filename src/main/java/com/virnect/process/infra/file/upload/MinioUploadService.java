package com.virnect.process.infra.file.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

import io.minio.MinioClient;
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

import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"local", "develop", "onpremise"})
@Component
@RequiredArgsConstructor
public class MinioUploadService implements FileUploadService {
	private final MinioClient minioClient;

	private static String CONTENT_DIRECTORY = "contents";
	private static String REPORT_DIRECTORY = "report";
	private static String REPORT_FILE_EXTENSION = ".jpg";

	@Value("${minio.bucket}")
	private String bucketName;

	@Value("${minio.bucket-resource}")
	private String bucketResource;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	@Value("${file.prefix}")
	private String prefix;

	@Override
	public boolean delete(String url) {
		if (!StringUtils.hasText(url) || url.contains("virnect_target.png")) {
			log.info("[MINIO FILE DELETE] DEFAULT FILE SKIP. URL : {}", url);
			return true;
		}
		String[] fileSplit = url.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];

		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucketName)
			.object(objectName)
			.build();
		log.info("[MINIO FILE DELETE] DELETE REQUEST. BUCKET : {}, KEY : {}", bucketName, objectName);
		try {
			minioClient.removeObject(removeObjectArgs);
			log.info(FilenameUtils.getName(url) + " 파일이 삭제되었습니다.");
			return true;
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {
			log.error(exception.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DELETE);
		}

	}

	@Override
	public String base64ImageUpload(String base64Image) {
		try {
			byte[] image = Base64.getDecoder().decode(base64Image);
			String randomFileName = String.format(
				"%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
				REPORT_FILE_EXTENSION
			);
			String objectName = String.format("%s%s/%s", bucketResource, REPORT_DIRECTORY, randomFileName);

			InputStream inputStream = new ByteArrayInputStream(image);
			PutObjectArgs putObjectArgs = PutObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.stream(inputStream, image.length, -1)
				.build();
			minioClient.putObject(putObjectArgs);
			return minioClient.getObjectUrl(bucketName, objectName);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_UPLOAD);
		}
	}

	@Override
	public String getFilePath(String fileName) {
		String objectName = bucketResource + fileName;
		try {
			String objectUrl = minioClient.getObjectUrl(bucketName, objectName);
			return objectUrl;
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
			ServerException | XmlParserException | IOException exception) {
			log.error(exception.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DOWNLOAD);
		}
	}

}
