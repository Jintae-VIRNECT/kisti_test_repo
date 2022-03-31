package com.virnect.download.application.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import lombok.extern.slf4j.Slf4j;

import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.error.ErrorCode;

@Service
@Slf4j
public class ApkService {
	private static final String FILE_DIR = "tmp";

	public ApkMeta parsingAppInfo(MultipartFile app) {
		if (app == null || app.isEmpty()) {
			log.error("Upload Application file is empty or set as null");
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
		try {
			ApkMeta apkMeta = getApkMeta(app);
			log.info(apkMeta.toString());
			return apkMeta;
		} catch (Exception e) {
			log.error("APK Parsing Error", e);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
	}

	private ApkMeta getApkMeta(MultipartFile multipartFile) {
		File file = new File(FILE_DIR + "/" + multipartFile.getOriginalFilename());
		log.info("[APK_SERVICE] FILE CONVERT TO APK FILE. NAME : {}", multipartFile.getOriginalFilename());
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			IOUtils.copy(multipartFile.getInputStream(), fileOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
			deleteFile(file);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}

		try (ApkFile apkFile = new ApkFile(file)) {
			return apkFile.getApkMeta();
		} catch (IOException e) {
			e.printStackTrace();
			deleteFile(file);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		} finally {
			deleteFile(file);
		}
	}

	private void deleteFile(File fIle) {
		if (fIle.delete()) {
			log.info("[APK_SERVICE] FILE DELETED.");
		} else {
			log.info("[APK_SERVICE] FILE NOT DELETED.");
		}

	}

}
