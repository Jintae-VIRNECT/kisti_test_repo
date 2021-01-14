package com.virnect.download.application;

import com.virnect.download.dao.DeviceRepository;
import com.virnect.download.domain.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project: PF-Download
 * DATE: 2021-01-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
class AppServiceTest {
    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void queryTest(){
        Device device = deviceRepository.findByTypeAndProduct_Name("MOBILE", "REMOTE").orElse(null);
        assertNotNull(device,"device는 null이 아니어야 한다.");

    }

}
