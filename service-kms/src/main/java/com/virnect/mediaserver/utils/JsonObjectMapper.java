package com.virnect.mediaserver.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public JsonObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();

        registerModule(simpleModule);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //Ignore unknown field
    }
}
