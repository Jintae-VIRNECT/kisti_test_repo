package com.virnect.download.application.download;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.dongliu.apk.parser.ByteArrayApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import lombok.extern.slf4j.Slf4j;

import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.error.ErrorCode;

@Service
@Slf4j
public class ApkService {
	public ApkMeta parsingAppInfo(MultipartFile app) {
		if (app == null || app.isEmpty()) {
			log.error("Upload Application file is empty or set as null");
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
		try (ByteArrayApkFile apkFile = new ByteArrayApkFile(app.getBytes())) {
			ApkMeta apkMeta = apkFile.getApkMeta();
			log.info(apkMeta.toString());
			return apkMeta;
		} catch (Exception e) {
			log.error("APK Parsing Error", e);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
	}
}
