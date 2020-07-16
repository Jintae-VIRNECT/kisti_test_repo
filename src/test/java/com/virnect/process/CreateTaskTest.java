package com.virnect.process;

import com.virnect.process.exception.ProcessServiceException;
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
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description 작업 생성 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CreateTaskTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void createTask_InvalidContentUUID_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/task")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"contentUUID\": \"da67f860-8462-11ea-bc55-0242ac130003\",\n" +
                        "  \"name\": \"작업명\",\n" +
                        "  \"ownerUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\",\n" +
                        "  \"startDate\": \"2020-11-16T11:20:33\",\n" +
                        "  \"endDate\": \"2020-12-16T12:20:33\",\n" +
                        "  \"position\": \"작업 위치 코멘트\",\n" +
                        "  \"targetType\": \"QR\",\n" +
                        "  \"workspaceUUID\": \"4d6eab0860969a50acbfa4599fbb5ae8\",\n" +
                        "  \"targetSetting\": \"duplicate\",\n" +
                        "  \"subTaskList\": [\n" +
                        "    {\n" +
                        "      \"id\": \"837bd35e-f035-4615-8f61-48d100e0feb0\",\n" +
                        "      \"name\": \"하위작업명\",\n" +
                        "      \"priority\": 1,\n" +
                        "      \"startDate\": \"2020-11-16T13:14:02\",\n" +
                        "      \"endDate\": \"2020-12-16T14:14:02\",\n" +
                        "      \"workerUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5901")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }

    @Test
    @Transactional
    public void createTask_ContentUUIDisNULL_ProcessServiceException() throws Exception {
        RequestBuilder request = post("/tasks/task")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"contentUUID\": \"\",\n" +
                        "  \"name\": \"작업명\",\n" +
                        "  \"ownerUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\",\n" +
                        "  \"startDate\": \"2020-11-16T11:20:33\",\n" +
                        "  \"endDate\": \"2020-12-16T12:20:33\",\n" +
                        "  \"position\": \"작업 위치 코멘트\",\n" +
                        "  \"targetType\": \"QR\",\n" +
                        "  \"workspaceUUID\": \"4d6eab0860969a50acbfa4599fbb5ae8\",\n" +
                        "  \"targetSetting\": \"duplicate\",\n" +
                        "  \"subTaskList\": [\n" +
                        "    {\n" +
                        "      \"id\": \"837bd35e-f035-4615-8f61-48d100e0feb0\",\n" +
                        "      \"name\": \"하위작업명\",\n" +
                        "      \"priority\": 1,\n" +
                        "      \"startDate\": \"2020-11-16T13:14:02\",\n" +
                        "      \"endDate\": \"2020-12-16T14:14:02\",\n" +
                        "      \"workerUUID\": \"498b1839dc29ed7bb2ee90ad6985c608\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("8001")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }
}
