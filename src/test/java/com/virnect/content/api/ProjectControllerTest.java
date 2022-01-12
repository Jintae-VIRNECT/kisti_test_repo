package com.virnect.content.api;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.dto.request.FileResourceUploadRequest;
import com.virnect.content.dto.request.ProjectUploadRequest;
import com.virnect.content.dto.request.PropertyInfoDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;

import com.virnect.content.domain.project.UpdateType;
import com.virnect.content.dto.request.EditPermissionRequest;
import com.virnect.content.dto.request.SharePermissionRequest;
import com.virnect.content.dto.request.ProjectTargetRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.virnect.content.dto.request.ProjectUpdateRequest;
import com.virnect.content.dto.response.FileResourceUploadResponse;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.FileResourceType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("update project name")
	void updateProject() throws Exception {
		String projectUUID = "10ecd5af-4ac8-4811-a157-0964cfae7725";
		String userUUID = "498b1839dc29ed7bb2ee90ad6985c608";
		String url = "/contents/projects/" + projectUUID;
		ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest();
		projectUpdateRequest.setType(UpdateType.UPDATE);
		projectUpdateRequest.setUserUUID(userUUID);
		projectUpdateRequest.setName("변경할 프로젝트 이름");
		projectUpdateRequest.setModeList(getProjectModeRequest());
		projectUpdateRequest.setShare(getSharePermissionRequest());
		projectUpdateRequest.setEdit(getEditPermissionRequest());
		projectUpdateRequest.setTarget(getProjectQRTargetRequest());
		projectUpdateRequest.setProject(getProject());

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(projectUpdateRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.result").value("true"))
			.andReturn();
	}

	@Test
	@DisplayName("upload project ")
	void uploadProject() throws Exception {
		String userUUID = "4a65aa94523efe5391b0541bbbcf97a3";
		String workspaceUUID = "4560ff80baf346af946f48b037f5af6b";
		String url = "/contents/projects";

		ProjectUploadRequest projectUploadRequest = new ProjectUploadRequest();
		projectUploadRequest.setWorkspaceUUID(workspaceUUID);
		projectUploadRequest.setUserUUID(userUUID);
		projectUploadRequest.setName("프로젝트 이름");
		projectUploadRequest.setProject(getProject());
		projectUploadRequest.setProperties(getPropertyInfoDTO());
		projectUploadRequest.setTarget(getProjectVTargetRequest());
		projectUploadRequest.setModeList(getProjectModeRequest());
		projectUploadRequest.setShare(getSharePermissionRequest());
		projectUploadRequest.setEdit(getEditPermissionRequest());

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(projectUploadRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	String getProject() throws Exception {
		String url = "/contents/resources";
		MockMultipartFile file
			= new MockMultipartFile(
			"file",
			"hello.mars",
			MediaType.TEXT_PLAIN_VALUE,
			"Hello, World!".getBytes()
		);

		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(url)
					.file(file)
					.param("type", FileResourceType.PROJECT.name()))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		return JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.data.uploadedUrl");
	}

	private List<Mode> getProjectModeRequest() {
		List<Mode> modeList = new ArrayList<>();
		modeList.add(Mode.THREE_DIMENSINAL);
		return modeList;
	}

	private EditPermissionRequest getEditPermissionRequest() {
		EditPermissionRequest editPermissionRequest = new EditPermissionRequest();
		editPermissionRequest.setPermission(EditPermission.MEMBER);
		return editPermissionRequest;
	}

	private SharePermissionRequest getSharePermissionRequest() {
		SharePermissionRequest sharePermissionRequest = new SharePermissionRequest();
		sharePermissionRequest.setPermission(SharePermission.MEMBER);
		return sharePermissionRequest;
	}

	private ProjectTargetRequest getProjectVTargetRequest() {
		ProjectTargetRequest projectTargetRequest = new ProjectTargetRequest();
		projectTargetRequest.setType(TargetType.VTarget);
		projectTargetRequest.setData("0f518d23-9226-4c8d-a488-c6581ef90456");
		projectTargetRequest.setLength(10L);
		projectTargetRequest.setWidth(10L);
		return projectTargetRequest;
	}

	private ProjectTargetRequest getProjectQRTargetRequest() {
		ProjectTargetRequest projectTargetRequest = new ProjectTargetRequest();
		projectTargetRequest.setType(TargetType.QR);
		projectTargetRequest.setData("0f518d23-9226-4c8d-a488-c6581ef90456");
		projectTargetRequest.setLength(10L);
		projectTargetRequest.setWidth(10L);
		return projectTargetRequest;
	}

	private PropertyInfoDTO getPropertyInfoDTO() {
		PropertyInfoDTO propertyInfoDTO = new PropertyInfoDTO();
		propertyInfoDTO.setPropertyName("propertyName");
		propertyInfoDTO.setPropertyObjectList(Lists.newArrayList());
		return propertyInfoDTO;
	}

	@Test
	@DisplayName("get project list")
	void getProjectList() throws Exception {
		String userUUID = "498b1839dc29ed7bb2ee90ad6985c608";
		String workspaceUUID = "4d6eab0860969a50acbfa4599fbb5ae8";
		String url = "/contents/projects?workspaceUUID=" + workspaceUUID + "&userUUID=" + userUUID;

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("get project list by member user")
	void getProjectListByMember() throws Exception {
		String userUUID = "273435b53ca1462d9e8ec58b78e5ad22";
		String workspaceUUID = "4d6eab0860969a50acbfa4599fbb5ae8";
		String url = "/contents/projects?workspaceUUID=" + workspaceUUID + "&userUUID=" + userUUID;

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("delete project")
	void deleteProject() throws Exception {
		String projectUUID = "10ecd5af-4ac8-4811-a157-0964cfae7725";
		String url = "/contents/projects/" + projectUUID;
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

		mockMvc.perform(
				MockMvcRequestBuilders
					.delete(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}

}
