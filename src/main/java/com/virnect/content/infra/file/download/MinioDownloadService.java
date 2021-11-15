package com.virnect.content.infra.file.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"develop", "onpremise", "test", "local"})
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MinioDownloadService implements FileDownloadService {
	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucketName;

	@Value("${minio.server}")
	private String minioServer;

	@Value("${file.prefix}")
	private String prefix;

	@Override
	public ResponseEntity<byte[]> fileDownload(String fileUrl, String range) {
		try {
			log.info("PARSER - URL: [{}]", fileUrl);
			String[] fileSplit = fileUrl.split(prefix);
			String objectName = fileSplit[fileSplit.length - 1];
			log.info("PARSER - KEY: [{}]", objectName);
			String fileName = FilenameUtils.getName(fileUrl);

			Map<String, String> headers = new HashMap<>();
			if (StringUtils.hasText(range)) {
				range = range.trim();
				if (!range.matches("^bytes=\\d*-\\d*$")) {
					log.error("Invalid Http Range : {}", range);
					throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
				}
				headers.put("Range", range);
			}
			GetObjectArgs getObjectArgs = GetObjectArgs.builder()
				.extraHeaders(headers)
				.bucket(bucketName)
				.object(objectName)
				.build();

			try (GetObjectResponse objectResponse = minioClient.getObject(getObjectArgs)) {
				byte[] bytes = IOUtils.toByteArray((InputStream)objectResponse);
				HttpHeaders httpHeaders = new HttpHeaders();
				String contentRange = objectResponse.headers().get("Content-Range");
				if (StringUtils.hasText(contentRange)) {
					httpHeaders.set("Content-Range", contentRange);
				}
				httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				httpHeaders.setContentLength(bytes.length);
				httpHeaders.setContentDisposition(
					ContentDisposition.builder("attachment").filename(fileName).build());
				return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
			} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
				ServerException | XmlParserException exception) {
				log.error(exception.getMessage());
				throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

	@Override
	public String getDefaultImagePath(String bucketResource, String fileName) {
		String objectName = bucketResource + fileName;
		try {
			GetObjectArgs getObjectArgs = GetObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.build();
			minioClient.getObject(getObjectArgs);
			return minioServer + "/" + bucketName + "/" + objectName;
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
			ServerException | XmlParserException | IOException exception) {
			log.error(exception.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

	@Override
	public long getFileSize(String fileUrl) {
		log.info("[MINIO GET FILE SIZE] GET SIZE BEGIN. URL : {}", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		log.info("[MINIO GET FILE SIZE] BUCKET : {}, KEY : {}", bucketName, objectName);
		GetObjectArgs getObjectArgs = GetObjectArgs.builder()
			.bucket(bucketName)
			.object(objectName)
			.build();
		long contentLength = 0L;
		try {
			GetObjectResponse getObjectResponse = minioClient.getObject(getObjectArgs);
			String contentLengthValue = getObjectResponse.headers().get("Content-Length");
			if (!StringUtils.hasText(contentLengthValue)) {
				log.error("[MINIO GET FILE SIZE] GET SIZE FAIL.");
				getObjectResponse.headers().names().forEach(key -> {
					String value = getObjectResponse.headers().get(key);
					log.error("HEADER NAME : [{}], VALUE : [{}]", key, value);
				});
				throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
			}
			contentLength = Long.parseLong(contentLengthValue);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
			log.error("[MINIO GET FILE SIZE] GET SIZE FAIL. ERROR MESSAGE : {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
		if (contentLength <= 0) {
			log.error("[MINIO GET FILE SIZE] GET SIZE FAIL. FILE SIZE : 0 (byte)");
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
		log.info("[MINIO GET FILE SIZE] GET SIZE SUCCESS. FILE SIZE : {} (byte)", contentLength);
		return contentLength;
	}

	@Override
	public byte[] getFileStreamBytes(String fileUrl) {
		log.info("[MINIO FILE DOWNLOAD] URL : {}", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		GetObjectArgs getObjectArgs = GetObjectArgs.builder()
			.bucket(bucketName)
			.object(objectName)
			.build();
		try (GetObjectResponse objectResponse = minioClient.getObject(getObjectArgs)) {
			return IOUtils.toByteArray((InputStream)objectResponse);
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException exception) {
			log.error(exception.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

	@Override
	public byte[] multipleFileDownload(List<String> fileUrlList) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			 ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			for (String fileUrl : fileUrlList) {
				log.info("[MINIO FILE DOWNLOAD] URL : {}", fileUrl);
				String[] fileSplit = fileUrl.split("/");
				String fileName = fileSplit[fileSplit.length - 1];
				byte[] fileStream = getFileStreamBytes(fileUrl);
				zipOutputStream.putNextEntry(new ZipEntry(fileName));
				zipOutputStream.write(fileStream);
				zipOutputStream.closeEntry();
			}
			zipOutputStream.finish();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

}
