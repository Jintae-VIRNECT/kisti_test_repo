package com.virnect.data.infra.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonUtil {
    /*public Props fromJsonObjectToProps(JsonObject params) {
        Props props = new Props();
        for (Map.Entry<String, JsonElement> entry : params.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                props.add(entry.getKey(), entry.getValue().getAsString());
            } else if (entry.getValue().isJsonObject()) {
                props.add(entry.getKey(), fromJsonObjectToProps(entry.getValue().getAsJsonObject()));
            }
        }
        return props;
    }*/

    public JsonObject fromInputStreamToJsonObject(InputStream inputStream)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException{
        return this.fromInputStreamToJsonElement(inputStream).getAsJsonObject();

    }

    public JsonObject fromFileToJsonObject(String filePath)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException {
        return this.fromFileToJsonElement(filePath).getAsJsonObject();
    }

    public JsonObject fromFileToJsonObject(File file)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException {
        return this.fromFileToJsonElement(file).getAsJsonObject();
    }

    public JsonArray fromFileToJsonArray(String filePath)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException {
        return this.fromFileToJsonElement(filePath).getAsJsonArray();
    }

    public JsonElement fromFileToJsonElement(String filePath)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException {
        JsonElement json = null;
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw e;
        }
        try {
            json = JsonParser.parseReader(reader);
        } catch (JsonParseException | IllegalStateException exception) {
            throw exception;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return json;
    }

    public JsonElement fromFileToJsonElement(File file)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException {
        JsonElement json = null;
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw e;
        }
        try {
            json = JsonParser.parseReader(reader);
        } catch (JsonParseException | IllegalStateException exception) {
            throw exception;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return json;
    }

    public JsonElement fromInputStreamToJsonElement(InputStream inputStream) {
        JsonElement json = null;
        try {
            json = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (JsonParseException | IllegalStateException exception) {
            throw exception;
        }
        return json;
    }
}
