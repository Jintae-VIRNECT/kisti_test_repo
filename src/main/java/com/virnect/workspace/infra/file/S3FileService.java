package com.virnect.workspace.infra.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"staging", "production"})
@Slf4j
@Service
public class S3FileService implements FileService {

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucket;

	@Value("${cloud.aws.s3.bucket.resource}")
	private String resource;

	@Value("${file.allow-extension}")
	private String allowExtension;

	private AmazonS3 amazonS3Client;

	public S3FileService(AmazonS3 amazonS3) {
		this.amazonS3Client = amazonS3;
	}

	@Override
	public String upload(MultipartFile multipartFile) throws IOException {
		String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();

		if (!allowExtension.contains(extension)) {
			log.error("Not Allow File Extension. Request File Extension >> {}", extension);
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
		File file = convert(multipartFile).orElseThrow(
			() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

		String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

		String objectName = resource + "/" + uniqueFileName;

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectName, file);
			putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);

			amazonS3Client.putObject(putObjectRequest);
			log.info("Upload File Info >> bucket : {}, resource : {}, filename : {}, fileSize : {}", bucket, resource,
				uniqueFileName, multipartFile.getSize()
			);
			removeNewFile(file);
			return amazonS3Client.getUrl(bucket, objectName).getPath();

		} catch (Exception e) {
			removeNewFile(file);
			log.error(e.getMessage());
			throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
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

	@Override
	public void delete(String fileUrl) {
		String fileName = FilenameUtils.getName(fileUrl);
		String endPoint = bucket + "/" + resource;
		amazonS3Client.deleteObject(endPoint, fileName);
		log.info(fileName + " 파일이 AWS S3(" + endPoint + ")에서 삭제되었습니다.");

	}

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

	@Override
	public String getFileUrl(String fileName) {
		String objectName = resource + "/" + fileName;
		return amazonS3Client.getUrl(bucket, objectName).getPath();
	}
}
