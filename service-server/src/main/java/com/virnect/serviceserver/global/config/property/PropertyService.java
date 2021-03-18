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
	/*//private final List<RemoteServiceConfig.Error> propertiesErrors = new ArrayList<>();
	public Map<String, String> configProps = new HashMap<>();
	public Map<String, ?> propertiesSource;

	private String remoteServicePublicUrl;
	private String remoteWebsocketUrl;
	public boolean turnadminAvailable;

	@Autowired
	protected Environment env;

	public PropertyService(){

	}


    *//*public PropertyService(Map<String, ?> propertiesSource) {
        this.propertiesSource = propertiesSource;
    }*//*

	*//*public void addError(String property, String msg) {
		String value = null;

		if (property != null) {
			value = getValue(property);
			//value = remoteServiceProperties.getValue(property);
		}
		this.propertiesErrors.add(new RemoteServiceConfig.Error(property, value, msg));
	}

	public List<RemoteServiceConfig.Error> getPropertiesErrors() {
		return propertiesErrors;
	}

	protected String getValue(String property) {
		return this.getValue(property, true);
	}

	protected String getValue(String property, boolean storeInConfigProps) {
		String value = null;
		if (propertiesSource != null) {
			Object valueObj = propertiesSource.get(property);
			if (valueObj != null) {
				value = valueObj.toString();
			}
		}
		if (value == null) {
			value = env.getProperty(property);
		}
		if (storeInConfigProps) {
			this.configProps.put(property, value);
		}
		return value;
	}

	protected String asNonEmptyString(String property, String stringValue) {
		if (stringValue != null && !stringValue.isEmpty()) {
			return stringValue;
		} else {
			addError(property, "Cannot be empty.");
			return null;
		}
	}

	protected String asOptionalString(String property) {
		return getValue(property);
	}

	protected Integer asNonNegativeInteger(String property, int value) {
		try {
			if (value < 0) {
				addError(property, "Is not a non negative integer");
			}
			return value;
		} catch (NumberFormatException e) {
			addError(property, "Is not a non negative integer");
			return 0;
		}
	}

	protected boolean asBoolean(String property, String value) {
		if (value == null) {
			addError(property, "Cannot be empty");
			return false;
		}
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(value);
		} else {
			addError(property, "Is not a boolean (true or false)");
			return false;
		}
	}

	protected <E extends Enum<E>> E asEnumValue(String property, Class<E> enumType) {
		String value = this.getValue(property);
		try {
			return Enum.valueOf(enumType, value);
		} catch (IllegalArgumentException e) {
			addError(property, "Must be one of " + Arrays.asList(enumType.getEnumConstants()));
			return null;
		}
	}*//*

	protected String asWritableFileSystemPath(String stringPath) {
		try {
			Paths.get(stringPath);
			File f = new File(stringPath);
			// set all permission granted
            *//*f.setReadable(true, false);
            f.setWritable(true, false);
            f.setExecutable(true, false);*//*

			f.getCanonicalPath();
			f.toURI().toString();
			if (!f.exists()) {
				if (!f.mkdirs()) {
					throw new Exception(
						"The path does not exist and RemoteService Server does not have enough permissions to create it");
				}
			}
			if (!f.canWrite()) {
				throw new Exception(
					"RemoteService Server does not have permissions to write on path " + f.getCanonicalPath());
			}
			stringPath = stringPath.endsWith("/") ? stringPath : (stringPath + "/");
			return stringPath;
		} catch (Exception e) {
			//addError(stringPath, "Is not a valid writable file system path. " + e.getMessage());
			return null;
		}
	}

	protected String asFileSystemPath(String stringPath) {
		try {
			Paths.get(stringPath);
			File f = new File(stringPath);
			f.getCanonicalPath();
			f.toURI().toString();
			stringPath = stringPath.endsWith("/") ? stringPath : (stringPath + "/");
			return stringPath;
		} catch (Exception e) {
			//addError(stringPath, "Is not a valid file system path. " + e.getMessage());
			return null;
		}
	}

	public List<String> asUriConference(List<String> urisConference) {
		for (String uri : urisConference) {
			try {
				this.checkWebsocketUri(uri).toString();
			} catch (Exception e) {
				//addError(property, uri + " is not a valid WebSocket URL");
			}
		}
		return urisConference;
	}

	public URI checkWebsocketUri(String uri) throws Exception {
		try {
			if (!uri.startsWith("ws://") || uri.startsWith("wss://")) {
				throw new Exception("WebSocket protocol not found");
			}
			String parsedUri = uri.replaceAll("ws://", "http://").replaceAll("wss://", "https://");
			return new URL(parsedUri).toURI();
		} catch (Exception e) {
			throw new RuntimeException(
				"URI '" + uri + "' has not a valid WebSocket endpoint format: " + e.getMessage());
		}
	}*/
}
