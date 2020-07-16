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
 * @description 작업 생성 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class EditTaskTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void editTask_InvalidTaskId_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/{taskId}", "200")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"taskId\": 200,\n" +
                        "  \"actorUUID\": \"4ffd52e92874da7392ffdf103c8d987e\",\n" +
                        "  \"startDate\": \"2020-01-16T11:20:33\",\n" +
                        "  \"endDate\": \"2020-01-16T12:20:33\",\n" +
                        "  \"position\": \"A 라인 2번 3번째 기계\",\n" +
                        "  \"subTaskList\": [\n" +
                        "    {\n" +
                        "      \"subTaskId\": 7,\n" +
                        "      \"startDate\": \"2020-01-16T13:14:02\",\n" +
                        "      \"endDate\": \"2020-01-16T14:14:02\",\n" +
                        "      \"workerUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5012")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }

    @Test
    @Transactional
    public void editTask_InvalidSubTaskId_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/{taskId}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"taskId\": 1,\n" +
                        "  \"actorUUID\": \"4ffd52e92874da7392ffdf103c8d987e\",\n" +
                        "  \"startDate\": \"2020-01-16T11:20:33\",\n" +
                        "  \"endDate\": \"2020-01-16T12:20:33\",\n" +
                        "  \"position\": \"A 라인 2번 3번째 기계\",\n" +
                        "  \"subTaskList\": [\n" +
                        "    {\n" +
                        "          \"subTaskId\": 200,\n" +
                        "          \"startDate\": \"2020-01-16T13:14:02\",\n" +
                        "          \"endDate\": \"2020-01-16T14:14:02\",\n" +
                        "          \"workerUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\"\n" +
                        "     }"+
                        "  ]\n" +
                        "}");


        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5012")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }
}
