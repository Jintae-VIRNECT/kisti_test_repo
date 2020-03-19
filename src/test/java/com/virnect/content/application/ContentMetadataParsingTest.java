package com.virnect.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-13
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class ContentMetadataParsingTest {
    public final String rawMetadata = "{\"aruco\":\"0\",\"contents\":[{\"id\":\"120\",\"name\":\"집에서 일안하고싶다아아으아아아가~~\",\"managerUUID\":\"0\",\"subProcessTotal\":\"0\",\"sceneGroups\":[{\"id\":\"0\",\"priority\":\"0\",\"name\":\"0\",\"jobTotal\":\"0\",\"scenes\":[{\"id\":\"0\",\"priority\":\"0\",\"name\":\"0\",\"subJobTotal\":\"0\",\"reportObjects\":[{\"id\":\"0\",\"items\":[{\"id\":\"0\",\"priority\":\"0\",\"type\":\"0\",\"title\":\"0\"}]}],\"smartToolObjects\":[{\"id\":\"0\",\"jobId\":\"0\",\"normalTorque\":\"0\",\"items\":[{\"id\":\"0\",\"batchCount\":\"0\"}]}]}]}]}]}";

    @Test
    public void parse() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> parseResultAsMap = objectMapper.readValue(rawMetadata, new TypeReference<Map<String, Object>>() {
//        });
//        System.out.println(parseResultAsMap);
//        parseResultAsMap.entrySet().stream().forEach(stringObjectEntry ->{
//            log.info("[{}] => [{}]", stringObjectEntry.getKey(), stringObjectEntry.getValue());
//        });
        ContentRestDto.RawMetadata contentInfo = objectMapper.readValue(rawMetadata, ContentRestDto.RawMetadata.class);
        System.out.println("=============================================================================================================================");
        System.out.println(contentInfo.toString());
        System.out.println("=============================================================================================================================");
    }
}
