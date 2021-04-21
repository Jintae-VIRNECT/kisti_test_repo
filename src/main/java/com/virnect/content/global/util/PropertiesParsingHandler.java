package com.virnect.content.global.util;

import com.google.gson.*;
import com.virnect.content.dto.PropertiesInfo;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-04-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
public class PropertiesParsingHandler {
    public PropertiesInfo getPropertiesRequest(String properties) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject propertyObj = (JsonObject) jsonParser.parse(properties);
            JsonObject propertyInfoObj = Objects.requireNonNull(propertyObj.getAsJsonObject("PropertyInfo"));
            String targetId = Objects.requireNonNull(propertyObj.get("TargetID").getAsString());
            Optional<JsonElement> jsonElement = Optional.ofNullable(propertyObj.get("TargetSize"));
            float targetSize = 10f;
            if (jsonElement.isPresent()) {
                targetSize = jsonElement.get().getAsFloat();
            }
            PropertiesInfo propertiesInfo = new PropertiesInfo();
            propertiesInfo.setId(targetId);
            propertiesInfo.setTargetSize(targetSize);
            List<PropertiesInfo.SceneGroup> sceneGroupList = getSceneGroups(propertyInfoObj);
            propertiesInfo.setSubProcessTotal(sceneGroupList.size());
            propertiesInfo.setSceneGroups(sceneGroupList);
            return propertiesInfo;
        } catch (NullPointerException | UnsupportedOperationException | JsonParseException e) {
            log.error("Properties parsing error. properties >> {}", properties);
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_METADATA_READ);
        }
    }

    private List<PropertiesInfo.SceneGroup> getSceneGroups(JsonObject propertyInfoObj) {
        List<PropertiesInfo.SceneGroup> sceneGroupList = new ArrayList<>();
        // 2-DEPTH
        int sceneGroupPriority = 0;
        for (Map.Entry<String, JsonElement> sceneGroupEntry : propertyInfoObj.entrySet()) {
            sceneGroupPriority++;
            JsonObject subPropertyObj = Objects.requireNonNull(sceneGroupEntry.getValue().getAsJsonObject());
            JsonObject subPropertyInfoObj = Objects.requireNonNull(subPropertyObj.getAsJsonObject("PropertyInfo"));
            Optional<JsonObject> jobObj = Optional.ofNullable(subPropertyObj.getAsJsonObject("Child"));

            PropertiesInfo.SceneGroup sceneGroup = new PropertiesInfo.SceneGroup();
            sceneGroup.setId(Optional.ofNullable(subPropertyInfoObj.get("identifier").getAsString()).orElse(""));
            sceneGroup.setName(Optional.ofNullable(subPropertyInfoObj.get("sceneGroupTitle").getAsString()).orElse("기본 하위 작업명"));
            sceneGroup.setPriority(sceneGroupPriority);

            if (jobObj.isPresent()) {
                List<PropertiesInfo.Scene> sceneList = getScenes(jobObj.get());
                sceneGroup.setJobTotal(jobObj.get().size());
                sceneGroup.setScenes(sceneList);
            } else {
                sceneGroup.setJobTotal(0);
                sceneGroup.setScenes(new ArrayList<>());
            }
            sceneGroupList.add(sceneGroup);
        }
        return sceneGroupList;
    }

    private List<PropertiesInfo.Scene> getScenes(JsonObject childObj) {
        List<PropertiesInfo.Scene> sceneList = new ArrayList<>();
        // 3-DEPTH
        int scenePriority = 0;
        for (Map.Entry<String, JsonElement> sceneEntry : childObj.entrySet()) {
            scenePriority++;
            JsonObject childPropertyObj = Objects.requireNonNull(sceneEntry.getValue().getAsJsonObject());
            JsonObject childPropertyInfoObj = Objects.requireNonNull(childPropertyObj.getAsJsonObject("PropertyInfo"));
            Optional<JsonObject> reportObj = Optional.ofNullable(childPropertyObj.getAsJsonObject("Child"));

            PropertiesInfo.Scene scene = new PropertiesInfo.Scene();
            scene.setId(Optional.ofNullable(childPropertyInfoObj.get("identifier").getAsString()).orElse(""));
            scene.setName(Optional.ofNullable(childPropertyInfoObj.get("sceneTitle").getAsString()).orElse("기본 단계명"));
            scene.setPriority(scenePriority);

            if (reportObj.isPresent()) {
                List<PropertiesInfo.ReportObject> reportObjectList = getReportObjects(reportObj.get());
                scene.setSubJobTotal(reportObj.get().size() == 0 ? 1 : reportObj.get().size());
                scene.setReportObjects(reportObjectList);
                //scene.setSmartToolObjects(new ArrayList<>());
            } else {
                scene.setSubJobTotal(1);
                scene.setReportObjects(new ArrayList<>());
                //scene.setSmartToolObjects(new ArrayList<>());
            }
            sceneList.add(scene);
        }
        return sceneList;
    }


    private List<PropertiesInfo.ReportObject> getReportObjects(JsonObject subChildObj) {
        List<PropertiesInfo.ReportObject> reportObjectList = new ArrayList<>();
        // 4-DEPTH
        for (Map.Entry<String, JsonElement> reportEntry : subChildObj.entrySet()) {
            JsonObject subChildPropertyObj = reportEntry.getValue().getAsJsonObject();
            JsonObject subChildPropertyInfoObj = Objects.requireNonNull(subChildPropertyObj.getAsJsonObject("PropertyInfo"));

            PropertiesInfo.ReportObject reportObject = new PropertiesInfo.ReportObject();
            // 5-DEPTH
            Optional<JsonElement> reportListItems = Optional.ofNullable(subChildPropertyInfoObj.get("reportListItems"));
            if (reportListItems.isPresent()) {
                List<PropertiesInfo.Item> itemList = new ArrayList<>();
                int itemPriority = 0;
                for (JsonElement jsonElement : reportListItems.get().getAsJsonArray()) {
                    itemPriority++;
                    PropertiesInfo.Item item = new PropertiesInfo.Item();
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    item.setId(jsonObject.get("identifier").getAsString());
                    item.setPriority(itemPriority);
                    item.setTitle(jsonObject.get("contents").getAsString());
                    //item.setType(ItemType.NONE); // 협의 필요
                    itemList.add(item);
                }
                reportObject.setId(subChildPropertyInfoObj.get("identifier").getAsString());
                reportObject.setItems(itemList);
            }
            reportObjectList.add(reportObject);
        }
        return reportObjectList;
    }
}
