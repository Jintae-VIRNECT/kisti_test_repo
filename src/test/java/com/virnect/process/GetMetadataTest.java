package com.virnect.process;

import com.virnect.process.exception.ProcessServiceException;
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
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description 메타데이터 조회 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
public class GetMetadataTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void getMetadata_InvalidTaskId_ProcessServiceException() throws Exception {
        RequestBuilder request = get("/tasks/metadata")
                .param("workerUUID", "498b1839dc29ed7bb2ee90ad6985c608")
                .param("tasksId", "51");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5005")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }
}