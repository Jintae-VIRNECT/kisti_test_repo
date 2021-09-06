package com.virnect.content.dto.request;

import java.util.List;

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
		private PropertyObjectType objectType;
		private List<ObjectChild> objectChildList;
	}

	public int getObjectCount(List<ObjectChild> propertyObjectList, int count) {
		for (int i = 0; i < propertyObjectList.size(); i++) {
			ObjectChild propertyObject = propertyObjectList.get(i);
			if (propertyObject.getObjectType() != PropertyObjectType.Scene
				&& propertyObject.getObjectType() != PropertyObjectType.SceneGroup) {
				count++;
			}
			if (!CollectionUtils.isEmpty(propertyObject.getObjectChildList())) {
				count = getObjectCount(propertyObject.getObjectChildList(), count);
			}
		}
		return count;
	}

	public int getSceneCount(List<ObjectChild> propertyObjectList, int count) {
		for (int i = 0; i < propertyObjectList.size(); i++) {
			ObjectChild propertyObject = propertyObjectList.get(i);
			if (propertyObject.getObjectType() == PropertyObjectType.Scene) {
				count++;
			}
			if (!CollectionUtils.isEmpty(propertyObject.getObjectChildList())) {
				count = getSceneCount(propertyObject.getObjectChildList(), count);
			}
		}
		return count;
	}

}
