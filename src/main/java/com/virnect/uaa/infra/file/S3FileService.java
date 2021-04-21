package com.virnect.uaa.infra.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.user.exception.UserServiceException;
import com.virnect.user.global.error.ErrorCode;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description AWS S3 file upload service class
 * @since 2020.03.20
 */

@Profile({"staging", "production"})
@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileService implements FileService {

	private final AmazonS3 amazonS3Client;
	@Value("${cloud.aws.s3.bucket.name:virnect-platform}")
	private String bucketName;
	@Value("${cloud.aws.s3.bucket.resource:profile}")
	private String bucketResource;

	@Override
	public String upload(MultipartFile file) throws IOException {
		final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;
		final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");

		log.info("[AWS S3 UPLOADER] - UPLOAD BEGIN");
		if (file.getSize() <= 0) {
			throw new UserServiceException(ErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
		}

		// 2. 확장자 검사
		String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
		if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
			log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
			throw new UserServiceException(ErrorCode.ERR_USER_PROFILE_IMAGE_EXTENSION);
		}

		// 3. 파일 용량 검사
		if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
			throw new UserServiceException(ErrorCode.ERR_USER_PROFILE_IMAGE_SIZE_LIMIT);
		}

		File uploadFile = convert(file)
			.orElseThrow(() -> {
				log.info("MultipartFile -> File 변환 실패");
				return new UserServiceException(ErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
			});

		String uniqueFileName = LocalDate.now() + "_" + UUID.randomUUID().toString().replace("-", "");

		String fileName = String.format("%s/%s%s", bucketResource, uniqueFileName, fileExtension);

		String uploadImageUrl = putS3(uploadFile, fileName);

		removeNewFile(uploadFile);

		return uploadImageUrl;
	}

	@Override
	public String upload(MultipartFile file, String fileName) throws IOException {
		return null;
	}

	@Override
	public boolean delete(String url) {
		if (Default.USER_PROFILE.isValueEquals(url)) {
			log.info("기본 이미지는 삭제하지 않습니다.");
			return false;
		} else {
			String resourceEndPoint = String.format("%s/%s", bucketName, bucketResource);
			String key = url.split(String.format("/%s/", bucketResource))[1];
			amazonS3Client.deleteObject(resourceEndPoint, key);
			log.info(key + " 파일이 AWS S3(" + resourceEndPoint + ")에서 삭제되었습니다.");
			return true;
		}
	}

	@Override
	public File getFile(String url) {
		return null;
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

}
