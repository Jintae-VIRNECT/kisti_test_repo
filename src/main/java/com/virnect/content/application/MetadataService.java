package com.virnect.content.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.dto.MetadataInfo;
import com.virnect.content.dto.response.MetadataInfoResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-08-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataService {
	private final ContentRepository contentRepository;
	private final ObjectMapper objectMapper;
	private final Gson gson;

	/**
	 * 콘텐츠 메타데이터 조회 요청 처리
	 *
	 * @param contentUUID - 콘텐츠 식별자
	 * @return - 콘텐츠 로우 메타데이터 및 콘텐츠 식별자 데이터
	 */
	@Transactional(readOnly = true)
	public ApiResponse<MetadataInfoResponse> getContentRawMetadata(String contentUUID) {
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
		try {
			MetadataInfoResponse metadataInfoResponse = this.objectMapper.readValue(
				content.getMetadata(), MetadataInfoResponse.class);
			metadataInfoResponse.getContents().setUuid(contentUUID);
			log.info("{}", content.toString());
			return new ApiResponse<>(metadataInfoResponse);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_METADATA_READ);
		}
	}

	/**
	 * 컨텐츠 properties -> metadata 변환 (1-DEPTH : 컨텐츠 변환)
	 * @param properties
	 * @param userId
	 * @param contentsName
	 * @return
	 */
	public MetadataInfo convertMetadata(String properties, String userId, String contentsName) {
		try {
			MetadataInfo metadataInfo = new MetadataInfo();
			// 1-DEPTH
			JsonParser jsonParser = new JsonParser();
			JsonObject propertyObj = (JsonObject)jsonParser.parse(properties);
			JsonObject propertyInfoObj = propertyObj.getAsJsonObject("PropertyInfo");
			String targetId = propertyObj.get("TargetID").getAsString();
			Optional<JsonElement> jsonElement = Optional.ofNullable(propertyObj.get("TargetSize"));
			float targetSize = 10f;
			if (jsonElement.isPresent()) {
				targetSize = jsonElement.get().getAsFloat();
			}

			MetadataInfo.Contents contents = new MetadataInfo.Contents();
			contents.setId(targetId);
			contents.setName(contentsName);
			contents.setManagerUUID(userId);
			contents.setTargetSize(targetSize);
			contents.setSubProcessTotal(propertyInfoObj.keySet().size());
			List<MetadataInfo.Scenegroup> scenegroupList = getSceneGroups(propertyInfoObj);
			contents.setSceneGroups(scenegroupList);

			metadataInfo.setContents(contents);
			//log.debug("Contents Property convert Metadata Result : {}", gson.toJson(metadataInfo));
			return metadataInfo;
		}  catch (NullPointerException | UnsupportedOperationException | JsonParseException e) {
			log.error("Properties parsing error. properties >> {}", properties);
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_METADATA_READ);
		}
	}

	/**
	 * 컨텐츠 properties -> metadata 변환 (2-DEPTH : 컨텐츠-SceneGroup 변환)
	 * @param propertyInfoObj
	 * @return
	 */
	private List<MetadataInfo.Scenegroup> getSceneGroups(JsonObject propertyInfoObj) {
		List<MetadataInfo.Scenegroup> scenegroupList = new ArrayList<>();
		// 2-DEPTH
		int sceneGroupPriority = 0;
		Set<Map.Entry<String, JsonElement>> sceneGroupEntrySet = propertyInfoObj.entrySet();
		for (Map.Entry<String, JsonElement> sceneGroupEntry : sceneGroupEntrySet) {
			sceneGroupPriority++;
			JsonObject subPropertyObj = sceneGroupEntry.getValue().getAsJsonObject();
			JsonObject subPropertyInfoObj = subPropertyObj.getAsJsonObject("PropertyInfo");
			Optional<JsonObject> childObj = Optional.ofNullable(subPropertyObj.getAsJsonObject("Child"));

			MetadataInfo.Scenegroup scenegroup = new MetadataInfo.Scenegroup();
			scenegroup.setId(Optional.ofNullable(subPropertyInfoObj.get("identifier").getAsString()).orElse(""));
			scenegroup.setName(
				Optional.ofNullable(subPropertyInfoObj.get("sceneGroupTitle").getAsString()).orElse("기본 하위 작업명"));
			scenegroup.setPriority(sceneGroupPriority);

			if (!childObj.isPresent()) {
				scenegroup.setJobTotal(0);
				scenegroup.setScenes(new ArrayList<>());
			} else {
				List<MetadataInfo.Scene> sceneList = getScenes(childObj);
				scenegroup.setJobTotal(childObj.get().size());
				scenegroup.setScenes(sceneList);
			}
			scenegroupList.add(scenegroup);
		}
		return scenegroupList;
	}

	/**
	 * 컨텐츠 properties -> metadata 변환 (3-DEPTH : 컨텐츠-SceneGroup-Scene 변환)
	 * @param childObj
	 * @return
	 */
	private List<MetadataInfo.Scene> getScenes(Optional<JsonObject> childObj) {
		List<MetadataInfo.Scene> sceneList = new ArrayList<>();
		// 3-DEPTH
		int scenePriority = 0;
		Set<Map.Entry<String, JsonElement>> sceneEntrySet = childObj.get().entrySet();
		for (Map.Entry<String, JsonElement> sceneEntry : sceneEntrySet) {
			scenePriority++;
			JsonObject childPropertyObj = sceneEntry.getValue().getAsJsonObject();
			JsonObject childPropertyInfoObj = childPropertyObj.getAsJsonObject("PropertyInfo");
			Optional<JsonObject> subChildObj = Optional.ofNullable(childPropertyObj.getAsJsonObject("Child"));

			MetadataInfo.Scene scene = new MetadataInfo.Scene();
			scene.setId(Optional.ofNullable(childPropertyInfoObj.get("identifier").getAsString()).orElse(""));
			scene.setName(Optional.ofNullable(childPropertyInfoObj.get("sceneTitle").getAsString()).orElse("기본 단계명"));
			scene.setPriority(scenePriority);

			if (!subChildObj.isPresent()) {
				scene.setSubJobTotal(1);
				scene.setReportObjects(new ArrayList<>());
				//scene.setSmartToolObjects(new ArrayList<>());
			} else {
				List<MetadataInfo.Reportobject> reportobjectList = getReportObjects(subChildObj);
				scene.setSubJobTotal(reportobjectList.size() == 0 ? 1 : reportobjectList.size());
				scene.setReportObjects(reportobjectList);
				//scene.setSmartToolObjects(new ArrayList<>());
			}
			sceneList.add(scene);
		}
		return sceneList;
	}

	/**
	 * 컨텐츠 properties -> metadata 변환 (4,5-DEPTH : 컨텐츠-SceneGroup-Scene-Report-Item 변환)
	 * @param subChildObj
	 * @return
	 */
	private List<MetadataInfo.Reportobject> getReportObjects(Optional<JsonObject> subChildObj) {
		List<MetadataInfo.Reportobject> reportobjectList = new ArrayList<>();
		// 4-DEPTH
		Set<Map.Entry<String, JsonElement>> reportEntrySet = subChildObj.get().entrySet();
		for (Map.Entry<String, JsonElement> reportEntry : reportEntrySet) {
			JsonObject subChildPropertyObj = reportEntry.getValue().getAsJsonObject();
			JsonObject subChildPropertyInfoObj = subChildPropertyObj.getAsJsonObject("PropertyInfo");
			MetadataInfo.Reportobject reportobject = new MetadataInfo.Reportobject();

			// 5-DEPTH
			Optional<JsonElement> reportListItems = Optional.ofNullable(subChildPropertyInfoObj.get("reportListItems"));
			if (reportListItems.isPresent()) {
				List<MetadataInfo.Item> itemList = new ArrayList<>();
				int itemPrority = 0;
				for (JsonElement jsonElement : reportListItems.get().getAsJsonArray()) {
					itemPrority++;
					MetadataInfo.Item item = new MetadataInfo.Item();
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					item.setId(jsonObject.get("identifier").getAsString());
					item.setPriority(itemPrority);
					item.setTitle(jsonObject.get("contents").getAsString());
					item.setItem("NONE"); // 협의 필요
					itemList.add(item);
				}
				reportobject.setId(subChildPropertyInfoObj.get("identifier").getAsString());
				reportobject.setItems(itemList);
			}
			reportobjectList.add(reportobject);
		}
		return reportobjectList;
	}
}
