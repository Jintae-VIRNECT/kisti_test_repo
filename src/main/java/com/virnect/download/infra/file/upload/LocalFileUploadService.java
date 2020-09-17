package com.virnect.download.infra.file.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;

import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.error.ErrorCode;

@Slf4j
@Service
@Profile(value = {"local"})
public class LocalFileUploadService implements FileUploadService {
	@Value("${file.upload-path}")
	private String path;
	@Value("${file.url}")
	private String url;
	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> allowedExtension;

	@Override
	public String upload(MultipartFile file) throws IOException {
		log.info("[AWS S3 UPLOADER] - UPLOAD BEGIN");
		if (file.getSize() <= 0) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_EMPTY_APPLICATION_FILE);
		}

		// 1. 확장자 검사
		String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
		if (!allowedExtension.contains(fileExtension)) {
			log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT);
		}

		// 2. 파일 복사
		File convertFile = new File(path + file.getOriginalFilename());
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
		}

		// 3. 파일 경로 추출
		String filePath = String.format("%s%s", url, convertFile.getPath()).replace("\\", "/");
		log.info("SAVE FILE_URL: {}", filePath);
		return filePath;
	}

	@Override
	public boolean delete(String url) {
		return false;
	}
}
