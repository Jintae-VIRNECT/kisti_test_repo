package com.virnect.process.infra.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

	@Override
	public String upload(MultipartFile file) throws IOException {
		return null;
	}

	@Override
	public String upload(MultipartFile file, String fileName) throws IOException {
		return null;
	}

	@Override
	public boolean delete(String url) {

		String objectName = bucketResource + REPORT_DIRECTORY + "/" + FilenameUtils.getName(url);
		RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
			.bucket(bucketName)
			.object(objectName)
			.build();
		try {
			minioClient.removeObject(removeObjectArgs);
			log.info(FilenameUtils.getName(url) + " 파일이 삭제되었습니다.");
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
			NoSuchAlgorithmException | ServerException | XmlParserException exception) {
			log.error(exception.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DELETE);
		}

		return true;
	}

	@Override
	public String getFileExtension(String originFileName) {
		return null;
	}

	@Override
	public boolean isAllowFileExtension(String fileExtension) {
		return false;
	}

	@Override
	public File getFile(String url) {
		return null;
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
		String objectName = bucketResource + REPORT_DIRECTORY + "/" + fileName;
		try {
			return minioClient.getObjectUrl(bucketName, objectName);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
			ServerException | XmlParserException | IOException exception) {
			log.error(exception.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_FILE_DOWNLOAD);
		}
	}

}
