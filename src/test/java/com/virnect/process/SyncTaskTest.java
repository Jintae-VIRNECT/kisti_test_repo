package com.virnect.process;

import com.virnect.process.exception.ProcessServiceException;
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
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description 수행결과 업로드(동기화) 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class SyncTaskTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void syncTask_InvalidSyncUserId_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"tasks\": [\n" +
                        "    {\n" +
                        "      \"id\": 1,\n" +
                        "      \"subTasks\": [\n" +
                        "        {\n" +
                        "          \"id\": 1,\n" +
                        "          \"priority\": 2,\n" +
                        "          \"syncUserUUID\": \"498b1839dc29ed7bb2ee90ad6\",\n" +
                        "          \"steps\": [\n" +
                        "            {\n" +
                        "              \"id\": 179,\n" +
                        "              \"isReported\": \"YES\",\n" +
                        "              \"result\": \"OK\",\n" +
                        "              \"reports\": [],\n" +
                        "              \"issues\": [\n" +
                        "                {\n" +
                        "                  \"photoFile\": \"\",\n" +
                        "                  \"caption\": \"\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]" +
                        "}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5101")))    // Process work result synchronization failed. Doesn't Match syncUser.
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }

    @Test
    @Transactional
    public void syncTask_InvalidTaskId_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"tasks\": [\n" +
                        "    {\n" +
                        "      \"id\": 2002,\n" +
                        "      \"subTasks\": [\n" +
                        "        {\n" +
                        "          \"id\": 105,\n" +
                        "          \"priority\": 2,\n" +
                        "          \"syncUserUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\",\n" +
                        "          \"steps\": [\n" +
                        "            {\n" +
                        "              \"id\": 179,\n" +
                        "              \"isReported\": \"YES\",\n" +
                        "              \"result\": \"OK\",\n" +
                        "              \"reports\": [],\n" +
                        "              \"issues\": [\n" +
                        "                {\n" +
                        "                  \"photoFile\": \"\",\n" +
                        "                  \"caption\": \"\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]" +
                        "}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5004")))    // Could not found process.
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }

    @Test
    @Transactional
    public void syncTask_InvalidStepId_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"tasks\": [\n" +
                        "    {\n" +
                        "      \"id\": 1,\n" +
                        "      \"subTasks\": [\n" +
                        "        {\n" +
                        "          \"id\": 1,\n" +
                        "          \"priority\": 2,\n" +
                        "          \"syncUserUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\",\n" +
                        "          \"steps\": [\n" +
                        "            {\n" +
                        "              \"id\": 20000,\n" +
                        "              \"isReported\": \"YES\",\n" +
                        "              \"result\": \"OK\",\n" +
                        "              \"reports\": [],\n" +
                        "              \"issues\": [\n" +
                        "                {\n" +
                        "                  \"photoFile\": \"\",\n" +
                        "                  \"caption\": \"\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]" +
                        "}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5100")))    // Process work result synchronization failed.
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }
}
