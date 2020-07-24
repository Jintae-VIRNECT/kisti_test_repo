package com.virnect.content;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class SplitUrlTest {
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.resource}")
    private String bucketResource;

    private static String url = "https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/workspace/contents/41edad4e-556b-4410-bafa-cbd5473f7c81.Ares";

    @Test
    public void match() {
        log.info(bucketName);
        log.info(bucketResource);
        String bucketPath = url.split(bucketResource)[1];
        String[] urls = bucketPath.split("/");
        for (String url : urls) {
            log.info(url);
        }
    }
}
