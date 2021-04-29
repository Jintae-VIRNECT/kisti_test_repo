package com.virnect.content.infra.file.upload;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.util.AES256EncryptUtils;
import com.virnect.content.global.util.QRcodeGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-04-09
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
@Profile("test")
class MinioUploadServiceTest {

    @Autowired
    MinioUploadService minioUploadService;

    @SneakyThrows
    @Test
    void base64ImageUpload() throws Exception {
        String originTargetData = "280%2fsMQ%2bE7d1CeudGF7wzx4uSi7DVfHGDfFNJzocQ5SBdcSYzXPJfuUtIPcGmTT%2b"; //타겟데이터의 껍데기값. db에 저장되는 값.
        String decoder = URLDecoder.decode(originTargetData, "UTF-8");
        String targetData = AES256EncryptUtils.decryptByBytes("virnect", decoder); //타겟데이터의 알맹이값. 유저식별자_랜덤키값으로 구성

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            BufferedImage qrImage = QRcodeGenerator.generateQRCodeImage(targetData, 256, 256);
            ImageIO.write(qrImage, "png", os);

            String qrString = Base64.getEncoder().encodeToString(os.toByteArray());
            minioUploadService.base64ImageUpload(qrString);
        } catch (Exception e) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }

    }
}