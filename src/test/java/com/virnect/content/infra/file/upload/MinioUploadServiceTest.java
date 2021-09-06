package com.virnect.content.infra.file.upload;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import com.virnect.content.infra.file.download.FileDownloadService;
import com.virnect.content.infra.file.download.MinioDownloadService;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
class MinioUploadServiceTest {
	private static final String REPORT_DIRECTORY = "report";
	private static final String VTARGET_FILE_NAME = "virnect_target.png";

	@Test
	void test() {

		String fileUrl = "https://192.168.6.3:2838/virnect-platform/workspace/report/virnect_target.png";
		String sourceFileExtension = fileUrl.substring(fileUrl.lastIndexOf(".") +1); //Ares
		System.out.println(sourceFileExtension);
		String[] split = fileUrl.split("/");
		String substring = fileUrl.substring(fileUrl.lastIndexOf("/"));
		System.out.println(substring);
		String substring2 = fileUrl.substring(fileUrl.lastIndexOf("/")-1);
		System.out.println(substring2);
		String substring3 = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		System.out.println(substring3);
	}

}