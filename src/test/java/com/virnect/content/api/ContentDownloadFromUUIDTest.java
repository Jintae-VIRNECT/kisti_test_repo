package com.virnect.content.api;

import com.virnect.content.exception.ContentServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 타겟 데이터로 컨텐츠 다운로드 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class ContentDownloadFromUUIDTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void contentDownload_contentNotFound_ContentServiceException() throws Exception {

        RequestBuilder request = get("/contents/download/contentUUID/{contentUUID}", "ad4e200d-85ec-44ae-bf5e-be9135")
                .param("memberUUID", "12345");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4003")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentDownload_contentUUIDIsNull_ContentServiceException() throws Exception {

        RequestBuilder request = get("/contents/download/contentUUID/{contentUUID}")
                .param("memberUUID", "12345");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4003")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }
}