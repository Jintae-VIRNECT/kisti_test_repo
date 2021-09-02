package com.virnect.content.infra.file.upload;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

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
		String[] split = fileUrl.split("/");
		System.out.println(this.getClass().getSimpleName());
		System.out.println(this.getClass().getEnclosingMethod().getName());
	}

}