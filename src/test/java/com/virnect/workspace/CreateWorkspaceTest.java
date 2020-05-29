package com.virnect.workspace;

import com.virnect.workspace.exception.WorkspaceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Project: PF-Workspace
 * DATE: 2020-05-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class CreateWorkspaceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createWorkspace_SecondWorkspace_WorkspaceException() throws Exception {
        // given
        RequestBuilder request = post("/workspaces")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("userId", "498b1839dc29ed7bb2ee90ad6985c608")
                .param("name", "이름")
                .param("description", "설명");
        // when
        this.mockMvc.perform(request)
                .andDo(print())
                // then
                .andExpect((result -> assertTrue(result.getResponse().getContentAsString().contains("1001"))))//ㅎㅎ;;;
                .andExpect((result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(WorkspaceException.class))));
    }
}
