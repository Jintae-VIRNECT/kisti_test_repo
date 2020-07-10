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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class ContentTargetAddTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void contentTargetAdd_InvalidContentUUID_ContentServiceException() throws Exception {
        RequestBuilder request = post("/contents/target/{contentUUID}", "02a67ae7-a464-4e99-bf15-ddc948")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
                .param("targetType", "QR")
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4004")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentTargetAdd_ContentState_ContentServiceException() throws Exception {
        RequestBuilder request = post("/contents/target/{contentUUID}", "02a67ae7-a464-4e99-bf15-ddc948df2e85")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
                .param("targetType", "QR")
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4009")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentTargetAdd_InvalidUserUUID_ContentServiceException() throws Exception {
        RequestBuilder request = post("/contents/target/{contentUUID}", "98c81916-48db-46ab-ab28-37f8115a388b")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
                .param("targetType", "QR")
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4015")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentTargetAdd_DuplicateTargetData_ContentServiceException() throws Exception {
        RequestBuilder request = post("/contents/target/{contentUUID}", "98c81916-48db-46ab-ab28-37f8115a388b")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
                .param("targetType", "QR")
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4101")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }
}
