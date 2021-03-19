package com.virnect.serviceserver.global.config.property;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kurento.jsonrpc.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.infra.utils.JsonUtil;

/**
 *
 */
public abstract class PropertyService {

	public Map<String, ?> propertiesSource;

	protected Object getValue(String propertyName, Object propertyValue) {
		Object value = null;
		if (propertiesSource != null) {
			Object valueObj = propertiesSource.get(propertyName);
			if (valueObj != null) {
				value = valueObj.toString();
			}
		} else {
			value = propertyValue;
		}
		return value;
	}
}
