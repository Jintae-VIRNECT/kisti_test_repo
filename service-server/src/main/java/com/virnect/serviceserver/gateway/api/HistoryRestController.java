package com.virnect.serviceserver.gateway.api;

import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media")
public class HistoryRestController {
    private static final Logger log = LoggerFactory.getLogger(HistoryRestController.class);
    private static final String TAG = "HistoryRestControllers";

    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @ApiOperation(value = "생략", notes = "생략")
    @GetMapping(value = "history")
    public ResponseEntity<?> getHistory(@RequestParam("name") String name,
                                        @RequestParam("page") int page) {
        log.info(TAG, "getHistory");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);

    }

    @ApiOperation(value = "생략", notes = "생략")
    @GetMapping(value = "history/{roomId}")
    public ResponseEntity<?> getHistoryByRoomId(@RequestParam("name") String name,
                                                @RequestParam("page") int page) {
        log.info(TAG, "getHistoryByRoomId");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "생략", notes = "생략")
    @DeleteMapping(value = "history")
    public ResponseEntity<?> deleteHistory(@RequestParam("name") String name,
                                           @RequestParam("page") int page) {
        log.info(TAG, "getHistoryByRoomId");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "생략", notes = "생략")
    @DeleteMapping(value = "history/{roomId}")
    public ResponseEntity<?> deleteHistoryByRoomId(@RequestParam("name") String name,
                                                   @RequestParam("page") int page) {
        log.info(TAG, "getHistoryByRoomId");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
    }

}
