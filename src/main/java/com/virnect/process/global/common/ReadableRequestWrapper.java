package com.virnect.process.global.common;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email sky456139@virnect.com
 * @since 2020.02.29
 */

@Slf4j
public class ReadableRequestWrapper extends HttpServletRequestWrapper {
    private final Charset encoding;
    private byte[] rawData;
    private Map<String, String[]> params = new HashMap<>();

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public ReadableRequestWrapper(HttpServletRequest request) {
        super(request);
        this.params.putAll(request.getParameterMap());
        String charEncoding = request.getCharacterEncoding();
        this.encoding = StringUtils.isBlank(charEncoding) ? StandardCharsets.UTF_8 : Charset.forName(charEncoding);

        try {
            InputStream is = request.getInputStream();
            this.rawData = ByteStreams.toByteArray(is);

            // Read Request Body
            String collect = this.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (StringUtils.isEmpty(collect)) {
                // Ignore Empty Body Request
                return;
            } else if (request.getContentType() != null && request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                // Ignore Logging Process File Upload Request
                return;
            }

            JsonParser jsonParser = new JsonParser();
            Object parse = jsonParser.parse(collect);
            if (parse instanceof JsonArray) {
                JsonArray jsonArray = (JsonArray) jsonParser.parse(collect);
                setParameter("REQUEST_BODY", jsonArray.toString());
            } else {
                JsonObject jsonObject = (JsonObject) jsonParser.parse(collect);
                for (String key : jsonObject.keySet()) {
                    setParameter(key, jsonObject.get(key).toString().replace("\"", "\\\""));
                }
            }

        } catch (Exception e) {
            log.error("Readable Request Wrapper Initialization Error", e);
        }
    }

    @Override
    public String getParameter(String name) {
        String[] paramArray = getParameterValues(name);
        if (paramArray == null || paramArray.length <= 0) {
            return null;
        }
        return paramArray[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] dummyParamValue = params.get(name);

        if (dummyParamValue != null) {
            result = new String[dummyParamValue.length];
            System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.length);
        }
        return result;
    }

    public void setParameter(String name, String value) {
        String[] param = {value};
        setParameter(name, param);
    }

    public void setParameter(String name, String[] values) {
        params.put(name, values);
    }


    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }
}
