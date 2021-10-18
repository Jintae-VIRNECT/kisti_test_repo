package com.virnect.process.infra.file.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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

	@Value("${cloud.aws.s3.bucket.resource}")
	private String bucketResource;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	@Value("${file.prefix}")
	private String prefix;

	private final AmazonS3 amazonS3Client;

	@Override
	public boolean delete(String url) {
		if (!StringUtils.hasText(url) || url.contains("virnect_target.png")) {
			log.info("[S3 DELETE] DEFAULT FILE SKIP. URL : {}", url);
			return true;
		}
		String[] fileSplit = url.split(prefix);
		String key = fileSplit[fileSplit.length - 1];

		log.info("[S3 FILE DELETE] DELETE REQUEST. BUCKET : {}, KEY : {}", bucketName, key);

		amazonS3Client.deleteObject(bucketName, key);
		log.info(FilenameUtils.getName(url) + " 파일이 AWS S3(" + bucketName + "/" + key + ")에서 삭제되었습니다.");
		return true;
	}

	@Override
	public String base64ImageUpload(String base64Image) {
		try {
			byte[] image = Base64.getDecoder().decode(base64Image);
			String randomFileName = String.format(
				"%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
				REPORT_FILE_EXTENSION
			);
			File convertImage = new File(randomFileName);
			FileOutputStream fos = new FileOutputStream(convertImage);
			fos.write(image);
			fos.close();

			String saveFileName = String.format("%s%s/%s", bucketResource, REPORT_DIRECTORY, randomFileName);
			String uploadFileUrl = putS3(convertImage, saveFileName);

			removeNewFile(convertImage);

			return uploadFileUrl;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
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
	public String getFilePath(String fileName) {
		String objectName = bucketResource + REPORT_DIRECTORY + "/" + fileName;
		log.info("[GET FILE PATH] Request objectName >> [{}]", objectName);
		String filePath = amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
		log.info("[GET FILE PATH] Response file Path >> [{}]", filePath);
		return filePath;
	}

}
