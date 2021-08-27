package com.virnect.content.global.common;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

import com.virnect.content.domain.PropertyObjectType;
import com.virnect.content.dto.request.PropertyInfoDTO;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
public class PropertyStringValidator implements ConstraintValidator<PropertyValidated, String> {

	@Override
	public boolean isValid(String properties, ConstraintValidatorContext context) {
		try {
			JsonParser jsonParser = new JsonParser();
			//1. json 파싱 검사
			jsonParser.parse(properties);
			//2. property 구조 파싱 검사
			ObjectMapper mapper = new ObjectMapper();
			PropertyInfoDTO propertyInfo = mapper.readValue(properties, PropertyInfoDTO.class);
			log.info("[PROPERTY VALIDATION CHECK] properties parsing result : {}", propertyInfo.toString());
			//3. 필수값 검사 - propertyName, propertyObjectList, sceneGroup
			if (!StringUtils.hasText(propertyInfo.getPropertyName())) {
				log.error("[PROPERTY VALIDATION CHECK] property name is blank. request properties : {}", properties);
				return false;
			}
			if (CollectionUtils.isEmpty(propertyInfo.getPropertyObjectList())) {
				log.error("[PROPERTY VALIDATION CHECK] scene group list is empty. request properties : {}", properties);
				return false;
			}
			if (propertyInfo.getPropertyObjectList()
				.stream()
				.noneMatch(objectChild -> objectChild.getObjectType() == PropertyObjectType.SceneGroup)) {
				log.error(
					"[PROPERTY VALIDATION CHECK] scene group is not first depth. request properties : {}", properties);
				return false;
			}
			return true;
		} catch (JsonSyntaxException | JsonProcessingException e) {
			log.error(
				"[PROPERTY VALIDATION CHECK] json parsing error. request properties : {}, error message : {}",
				properties, e.getMessage()
			);
			return false;
		}
	}
}
