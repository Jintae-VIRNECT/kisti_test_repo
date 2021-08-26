package com.virnect.workspace;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import com.virnect.workspace.exception.WorkspaceException;

/**
 * Project: PF-Workspace
 * DATE: 2020-05-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CreateWorkspaceTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createWorkspace_SecondWorkspace_WorkspaceException() throws Exception {
        // given
        RequestBuilder request = post("/workspaces")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .param("userId", "4b260e69bd6fa9a583c9bbe40f5aceb3")
            .param("name", "이름")
            .param("description", "설명");
        // when
        this.mockMvc.perform(request)
            .andDo(print())
            // then
            .andExpect((result -> assertTrue(result.getResponse().getContentAsString().contains("139"))));
    }
    @Test
    public void creete(){
    /*    for(int temp=635;temp<695;temp++){

            for (int a=1; a<7; a++){
                System.out.println("INSERT INTO `license` (`created_at`, `updated_at`, `serial_key`, `license_status`, `user_id`, `workspace_id`, `license_product_id`) VALUES ('2020-07-30 21:54:26', '2020-07-30 21:54:26', '"+
                        UUID.randomUUID().toString().toUpperCase()+"', 1, NULL, NULL, "+temp+");");

            }
        }
*/
        String url = "https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/workspace/contents/76c3b9f7-7e1c-4e8f-949e-c3ae631c9dc7.Ares";

        String resourceEndPoint = String.format("%s%s", "workspace/", "contents");
        //            String resourceEndPoint = String.format("%s/%s", bucketName, bucketResource);
        //String key = url.split(String.format("/%s/%s", "workspace/", "contents"))[1];
        String key = "workspace/" + "contents" + "/" + FilenameUtils.getName(url);
        System.out.println(resourceEndPoint);
        System.out.println(key);


    }


}
