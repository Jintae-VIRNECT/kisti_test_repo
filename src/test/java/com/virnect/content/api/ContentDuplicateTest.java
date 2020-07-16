package com.virnect.content.api;

import com.virnect.content.exception.ContentServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
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
 * @description 컨텐츠 복제 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class ContentDuplicateTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void contentDuplicate_InvalidContentUUID_ContentServiceException() throws Exception {
//        RequestBuilder request = post("/contents/duplicate/{contentUUID}", "0d668d4f-7a78-43c0-999d-f0e304acbd14")
        RequestBuilder request = post("/contents/duplicate/{contentUUID}", "6993fa89-7bff-4414-b186-d87")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608")
                .param("workspaceUUID", "4d6eab0860969a50acbfa4599fbb5ae8");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4003"))) // Content not found.
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentDuplicate_InvalidUserUUID_ContentServiceException() throws Exception {
        RequestBuilder request = post("/contents/duplicate/{contentUUID}", "6993fa89-7bff-4414-b186-d8719730f25f")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("userUUID", "498b1839dc29ed7bb2ee9")
                .param("workspaceUUID", "4d6eab0860969a50acbfa4599fbb5ae8");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4015"))) // An error occurred in the request. Because it is NOT ownership.
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentDuplicate_InvalidWorkspaceUUID_ContentServiceException() throws Exception {
//        RequestBuilder request = post("/contents/duplicate/{contentUUID}", "0d668d4f-7a78-43c0-999d-f0e304acbd14")
        RequestBuilder request = post("/contents/duplicate/{contentUUID}", "6993fa89-7bff-4414-b186-d8719730f25f")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608")
                .param("workspaceUUID", "4d6eab0860969a50ac");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4016")))  // An error occurred in the request. Because Workspace is different.
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }
}
