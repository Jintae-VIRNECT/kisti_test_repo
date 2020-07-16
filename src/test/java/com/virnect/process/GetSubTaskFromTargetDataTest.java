package com.virnect.process;

import com.virnect.process.exception.ProcessServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
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
 * @description 타겟 데이터의 하위 작업 목록 조회 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class GetSubTaskFromTargetDataTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void getSubTaskFromTargetData_InvalidTaskId_ProcessServiceException() throws Exception {
        RequestBuilder request = get("/tasks/target/{targetData}", "84-43e4-8fdd-e96da15fa182");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("5007")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ProcessServiceException.class)));
    }
}