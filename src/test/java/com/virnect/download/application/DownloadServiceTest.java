package com.virnect.download.application;

import com.virnect.download.dao.AppRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Project: PF-Download
 * DATE: 2020-05-26
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class DownloadServiceTest {
    @Test
    void createAppUUID() {
        String[] tokens = UUID.randomUUID().toString().split("-");
        System.out.println(tokens[1] + "-" +tokens[4]);

        String[] tokens2 = UUID.randomUUID().toString().split("-");
        System.out.println(tokens2[1] + "-" +tokens2[4]);

        String[] tokens3 = UUID.randomUUID().toString().split("-");
        System.out.println(tokens3[1] + "-" +tokens3[4]);

    }

}
