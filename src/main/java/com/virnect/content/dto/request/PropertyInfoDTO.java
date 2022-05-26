package com.virnect.content.dto.request;

import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.PropertyObjectType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-26
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class PropertyInfoDTO {
	private String propertyName;
	private List<ObjectChild> propertyObjectList;

	@Getter
	@Setter
	public static class ObjectChild {
		private String objectName;
		private String objectType;
		private List<ObjectChild> objectChildList;
	}

	public int getObjectCount(List<ObjectChild> propertyObjectList, int count) {
		for (ObjectChild propertyObject : propertyObjectList) {
			if (!Objects.equals(propertyObject.getObjectType(), PropertyObjectType.Scene.toString())
				&& !Objects.equals(propertyObject.getObjectType(), PropertyObjectType.SceneGroup.toString())) {
				count++;
			}
			if (!CollectionUtils.isEmpty(propertyObject.getObjectChildList())) {
				count = getObjectCount(propertyObject.getObjectChildList(), count);
			}
		}
		return count;
	}

	public int getSceneCount(List<ObjectChild> propertyObjectList, int count) {
		for (ObjectChild propertyObject : propertyObjectList) {
			if (Objects.equals(propertyObject.getObjectType(), PropertyObjectType.Scene.toString())) {
				count++;
			}
			if (!CollectionUtils.isEmpty(propertyObject.getObjectChildList())) {
				count = getSceneCount(propertyObject.getObjectChildList(), count);
			}
		}
		return count;
	}

}
