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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 컨텐츠 상세 정보 조회 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class ContentTargetUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void contentTargetUpdate_InvalidContentUUID_ContentServiceException() throws Exception {
        RequestBuilder request = put("/contents/target/{contentUUID}", "2573882c-9ad6-4a5f-94bd-")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("oldTargetId", "180")
                .param("targetData", "Shzf1q3pEsr4yt7Uc20e5GIueuEAkgel82sg6biCRtrkQBY%2bzTqplH4ZMHp4yxJw")
                .param("targetType", "QR")
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4004")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }

    @Test
    @Transactional
    public void contentTargetUpdate_ContentState_ContentServiceException() throws Exception {
        RequestBuilder request = put("/contents/target/{contentUUID}", "4e2cfebd-5b16-4dd6-96a4-f2c93e5e241e")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("oldTargetId", "136")
                .param("targetData", "Shzf1q3pEsr4yt7Uc20e5GIueuEAkgel82sg6biCRtrkQBY%2bzTqplH4ZMHp4yxJw")
                .param("targetType", "QR")
                .param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("4009")))
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
    }
}
