package com.virnect.content.infra.file.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
@Slf4j
@Profile({"staging", "production"})
@Component
@RequiredArgsConstructor
public class S3FileDownloadService implements FileDownloadService {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Override
	public ResponseEntity<byte[]> fileDownload(String fileUrl, String range) {
		String prefix = "https://" + bucketName + ".s3." + amazonS3Client.getRegionName() + ".amazonaws.com/";
		log.info("PARSER - URL: [{}]", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		log.info("PARSER - KEY: [{}]", objectName);
		String fileName = FilenameUtils.getName(fileUrl);

		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
		if (StringUtils.hasText(range)) {
			range = range.trim();
			if (!range.matches("^bytes=\\d*-\\d*$")) {
				log.error("Invalid Http Range : {}", range);
				throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
			}
			String[] requestRange = range.replace("bytes=", "").split("-");
			if (requestRange.length > 1) {
				getObjectRequest.setRange(Long.parseLong(requestRange[0]), Long.parseLong(requestRange[1]));
			} else {
				getObjectRequest.setRange(Long.parseLong(requestRange[0]));
			}
		}
		try (S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
			 S3ObjectInputStream objectInputStream = s3Object.getObjectContent()) {
			HttpHeaders httpHeaders = new HttpHeaders();
			Map<String, Object> metadata = s3Object.getObjectMetadata().getRawMetadata();
			String contentRange = (String)metadata.get(Headers.CONTENT_RANGE);
			if (contentRange != null) {
				httpHeaders.set("Content-Range", contentRange);
			}
			byte[] bytes = IOUtils.toByteArray(objectInputStream, s3Object.getObjectMetadata().getContentLength());
			httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			httpHeaders.setContentLength(bytes.length);
			httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
			return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
		} catch (AmazonS3Exception | IOException e) {
			log.error("Error Message:     {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

	@Override
	public String getDefaultImagePath(String bucketResource, String fileName) {
		log.info("[GET FILE PATH] Request BucketResource >> [{}], fileName >> [{}]", bucketResource, fileName);
		String objectName = bucketResource + fileName;
		String filePath = amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
		log.info("[GET FILE PATH] Response file Path >> [{}]", filePath);
		return filePath;
	}

	@Override
	public long getFileSize(String fileUrl) {
		String prefix = "https://" + bucketName + ".s3." + amazonS3Client.getRegionName() + ".amazonaws.com/";
		log.info("[AWS S3 GET FILE SIZE] GET SIZE BEGIN. URL : {}", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		log.info("[AWS S3 GET FILE SIZE] BUCKET : {}, KEY : {}", bucketName, objectName);
		long contentLength = 0L;
		try (S3Object object = amazonS3Client.getObject(bucketName, objectName)) {
			contentLength = object.getObjectMetadata().getContentLength();
		} catch (AmazonS3Exception e) {
			log.error("Caught an AmazonServiceException from GET requests, rejected reasons:");
			log.error("Error Message:     {}", e.getMessage());
			log.error("HTTP Status Code:  {}", e.getStatusCode());
			log.error("AWS Error Code:    {}", e.getErrorCode());
			log.error("Error Type:        {}", e.getErrorType());
			log.error("Request ID:        {}", e.getRequestId());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		} catch (IOException e) {
			log.error("Caught an AmazonServiceException from GET requests, rejected reasons:");
			log.error("Error Message:     {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
		if (contentLength <= 0) {
			log.error("[AWS S3 GET FILE SIZE] GET SIZE FAIL. FILE SIZE : 0 (byte)");
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
		log.info("[AWS S3 GET FILE SIZE] GET SIZE SUCCESS. FILE SIZE : {} (byte)", contentLength);
		return contentLength;
	}

	@Override
	public byte[] getFileStreamBytes(String fileUrl) {
		String prefix = "https://" + bucketName + ".s3." + amazonS3Client.getRegionName() + ".amazonaws.com/";
		log.info("[AWS S3 FILE DOWNLOAD] URL : {}", fileUrl);
		String[] fileSplit = fileUrl.split(prefix);
		String objectName = fileSplit[fileSplit.length - 1];
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectName);
		try (S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
			 S3ObjectInputStream objectInputStream = s3Object.getObjectContent()) {
			return IOUtils.toByteArray(objectInputStream);
		} catch (AmazonS3Exception | IOException e) {
			log.error("Error Message:     {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

	@Override
	public byte[] multipleFileDownload(List<String> fileUrlList) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			 ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			for (String fileUrl : fileUrlList) {
				log.info("[AWS S3 FILE DOWNLOAD] URL : {}", fileUrl);
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
