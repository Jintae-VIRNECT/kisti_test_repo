package com.virnect.serviceserver.gateway.api;

import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/remote")
public class SessionRestController {
/*
/media/sessions/{sessionId}/connection           publishIpcam
signal;
test;
//tokens <- 애만씀.*/

    /*private static final Logger log = LoggerFactory.getLogger(SessionRestController.class);
    private static final String TAG = "SessionRestController";

    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @ApiOperation(value = "생략", notes = "생략")
    @PostMapping(value = "tokens")
    public ResponseEntity<?> newToken(@RequestParam("name") String name,
                                      @RequestParam("page") int page) {
        log.info(TAG, "getHistory");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);

    }*/
}
