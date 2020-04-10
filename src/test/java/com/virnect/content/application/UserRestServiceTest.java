package com.virnect.content.application;

import com.virnect.content.application.user.UserRestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-06
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class UserRestServiceTest {
    @Autowired
    private UserRestService userRestService;

    @Test
    public void userInfoListRestServiceTest() {
        // given
//        ResponseMessage response = this.userRestService.getUserInfoSearch("smic");
//        log.info("[{}] => [{}]", LocalDateTime.now(), response);
    }

}
