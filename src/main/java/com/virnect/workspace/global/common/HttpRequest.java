package com.virnect.workspace.global.common;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class HttpRequest {
    public ArrayList<Map> getUserSearchList(UriComponents uriComponents) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ResponseEntity<Map> responseEntity = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, new HttpEntity<String>(headers), Map.class);
        LinkedHashMap data = (LinkedHashMap) responseEntity.getBody().get("data");
        ArrayList<Map> response = (ArrayList<Map>) data.get("userInfoList");

        return response;
    }

}
