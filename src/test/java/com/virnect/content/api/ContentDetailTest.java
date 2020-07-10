package com.virnect.content.api;

import com.virnect.content.exception.ContentServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 컨텐츠 상세 정보 조회 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class ContentDetailTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contentDetail_InvalidContentUUID_ContentServiceException() throws Exception {
        RequestBuilder request = get("/contents/{contentUUID}", "061cc38d-6c45-445b-bf56-4d164f")
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4003")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }
}
