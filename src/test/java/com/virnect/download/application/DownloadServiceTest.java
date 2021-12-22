package com.virnect.download.application;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

/**
 * Project: PF-Download
 * DATE: 2020-05-26
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
 class DownloadServiceTest {
	@Test
	void createAppUUID() {
		String[] tokens = UUID.randomUUID().toString().split("-");
		System.out.println(tokens[1] + "-" + tokens[4]);

		String[] tokens2 = UUID.randomUUID().toString().split("-");
		System.out.println(tokens2[1] + "-" + tokens2[4]);

		String[] tokens3 = UUID.randomUUID().toString().split("-");
		System.out.println(tokens3[1] + "-" + tokens3[4]);
	}
	@Test
	void test(){
		String s = StringUtils.deleteAny(".2.3.1", ".");
		System.out.println(s);
	}

}
