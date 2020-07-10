package com.virnect.content.api;

import com.virnect.content.exception.ContentServiceException;
import lombok.extern.slf4j.Slf4j;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 컨텐츠 삭제 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Slf4j
public class DeleteContentTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void deleteContent_convertedContents_test() throws Exception {

        RequestBuilder request = delete("/contents")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"contentUUIDs\": [\"72e10229-0be8-4c64-a319-7c7a9612fe23\", \"0d668d4f-7a78-43c0-999d-f0e304acbd14\"], \"workspaceUUID\":\"4d6eab0860969a50acbfa4599fbb5ae8\"}");
                //.content("contentDeleteRequest", "{\"contentUUIDs\": [\"72e10229-0be8-4c64-a319-7c7a9612fe23\", \"0d668d4f-7a78-43c0-999d-f0e304acbd14\"], \"workspaceUUID\":\"4d6eab0860969a50acbfa4599fbb5ae8\"}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Content deletion failed.")));
    }

    @Test
    @Transactional
    public void deleteContent_contentNotFound_test() throws Exception {
        RequestBuilder request = delete("/contents")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content("{\"contentUUIDs\": [\"72e10229-0be8-4c64-\", \"0d668d4f--999d-f0e304acbd14\"], \"workspaceUUID\":\"4d6eab0860969a50acbfa4599fbb5ae8\"}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Content not found.")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void deleteContent_workspace_test() throws Exception {
        RequestBuilder request = delete("/contents")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"contentUUIDs\": [\"72e10229-0be8-4c64-a319-7c7a9612fe23\", \"0d668d4f-7a78-43c0-999d-f0e304acbd14\"], \"workspaceUUID\":\"433\"}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Because Workspace is different.")));
    }
}
