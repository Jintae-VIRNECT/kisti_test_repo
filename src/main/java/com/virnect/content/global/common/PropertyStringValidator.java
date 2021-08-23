package com.virnect.content.global.common;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

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
			JsonObject jsonObject = (JsonObject)jsonParser.parse(properties);
			//2. 컨텐츠 or 프로젝트 이름 검사
			String name = jsonObject.get("name").toString();
			if (!StringUtils.hasText(name)) {
				log.error("[PROPERTY VALIDATION CHECK] property name is blank. request properties : {}", properties);
				return false;
			}

			//3. 씬 그룹 최소 1개 검사
			Optional<JsonArray> sceneGroupList = Optional.ofNullable(jsonObject.getAsJsonArray("sceneGroupList"));
			if (!sceneGroupList.isPresent() || sceneGroupList.get().size() == 0) {
				log.error("[PROPERTY VALIDATION CHECK] scene group list is empty. request properties : {}", properties);
				return false;
			}
			return true;
		} catch (JsonSyntaxException e) {
			log.error(
				"[PROPERTY VALIDATION CHECK] json parsing error. request properties : {}, error message : {}",
				properties, e.getMessage()
			);
			return false;
		}
	}
}
