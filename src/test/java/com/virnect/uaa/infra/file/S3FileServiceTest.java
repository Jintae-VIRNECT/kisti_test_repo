package com.virnect.uaa.infra.file;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.virnect.uaa.infra.file.config.S3TestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(S3TestConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class S3FileServiceTest {
	private final static String PATH = "src/test/resources/test_profile.png";
	private MockMultipartFile profileImage = new MockMultipartFile(
		"profileImage", "imagefile.png", "image/png", "<<png data>>".getBytes());

	@Autowired
	private FileService s3FileService;

	private static String imageUrl;

	@Test
	@Order(1)
	@DisplayName("S3 file upload test")
	void uploadToS3() {
		try {
			imageUrl = s3FileService.upload(profileImage);
			assertThat(imageUrl).isNotBlank();
			assertThat(imageUrl).contains(
				s3FileService.getExtensionFromFileName(profileImage.getOriginalFilename()));
			assertThat(imageUrl).contains(
				LocalDate.now().toString()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@Order(2)
	@DisplayName("S3 file delete test")
	void deleteFileOnS3() {
		boolean result = s3FileService.delete(imageUrl);
		assertThat(result).isTrue();
	}

}
