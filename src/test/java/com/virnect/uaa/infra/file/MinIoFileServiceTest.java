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

import com.virnect.uaa.infra.file.config.MinioTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(MinioTestConfiguration.class)
class MinIoFileServiceTest {
	private final static String PATH = "src/test/resources/test_profile.png";
	private MockMultipartFile profileImage = new MockMultipartFile(
		"profileImage", "imagefile.png", "image/png", "<<png data>>".getBytes());

	@Autowired
	private FileService minioFileService;

	private static String imageUrl = "";

	@Test
	@Order(1)
	@DisplayName("minIO file upload test")
	void uploadToMinIO() {
		try {
			imageUrl = minioFileService.upload(profileImage);
			assertThat(imageUrl).isNotBlank();
			assertThat(imageUrl).contains(
				minioFileService.getExtensionFromFileName(profileImage.getOriginalFilename()));
			assertThat(imageUrl).contains(
				LocalDate.now().toString()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@Order(2)
	@DisplayName("minIO file delete test")
	void deleteFileOnMinIO() {
		boolean result = minioFileService.delete(imageUrl);
		assertThat(result).isTrue();
	}

}
