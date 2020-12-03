package com.virnect.serviceserver.utils;

import com.google.gson.*;
import org.kurento.jsonrpc.Props;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

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

    public JsonObject fromFileToJsonObject(String filePath)
            throws IOException, FileNotFoundException, JsonParseException, IllegalStateException {
        return this.fromFileToJsonElement(filePath).getAsJsonObject();
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
}
