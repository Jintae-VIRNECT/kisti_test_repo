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
/*    @Autowired
    private MockMvc mockMvc;
    @Mock
    private AppRepository appRepository;

    @Test
    void uploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe",
                "multipart/form-data", "영원한 삶..".getBytes());
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/download/upload").file(file))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }*/

}
