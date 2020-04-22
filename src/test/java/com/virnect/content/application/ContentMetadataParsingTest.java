package com.virnect.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-13
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class ContentMetadataParsingTest {
    public final String rawMetadata = "{\"contents\":{\"id\":\"b5db6bb8-9976-4865-859c-1b98e57a3dc5\",\"name\":\"SampleContent\",\"managerUUID\":\"\",\"subProcessTotal\":1,\"sceneGroups\":[{\"id\":\"5f43e519-0f18-46c1-947e-198f801bf3cc\",\"priority\":1,\"name\":\"SceneGroup\",\"jobTotal\":4,\"scenes\":[{\"id\":\"0292b07c-414a-499d-82ee-ad14e2e40dc1\",\"priority\":1,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]},{\"id\":\"7cfda7c8-3a62-404a-9375-b30c23e45637\",\"priority\":2,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]},{\"id\":\"285c316d-d27c-4032-9cd0-638ab9f682e3\",\"priority\":3,\"name\":\"Scene\",\"subJobTotal\":7,\"reportObjects\":[{\"id\":\"e26735f0-3575-45ef-a9d5-4017ec4b01f1\",\"items\":[{\"id\":null,\"priority\":1,\"type\":\"TOGGLE\",\"title\":\"항목1\"},{\"id\":null,\"priority\":2,\"type\":\"INPUT_FIELD\",\"title\":\"항목2\"},{\"id\":null,\"priority\":3,\"type\":\"REPORT\",\"title\":\"항목3\"}]}]},{\"id\":\"c3604d08-cf2b-43f5-90df-b6b8715537d2\",\"priority\":4,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]}]}]}}";

    @Test
    public void parse() throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> parseResultAsMap = objectMapper.readValue(rawMetadata, new TypeReference<Map<String, Object>>() {
//        });
//        System.out.println(parseResultAsMap);
//        parseResultAsMap.entrySet().stream().forEach(stringObjectEntry ->{
//            log.info("[{}] => [{}]", stringObjectEntry.getKey(), stringObjectEntry.getValue());
//        });
//        ContentRestDto.RawMetadata contentInfo = objectMapper.readValue(rawMetadata, ContentRestDto.RawMetadata.class);
//        System.out.println("=============================================================================================================================");
//        System.out.println(contentInfo.toString());
//        System.out.println("=============================================================================================================================");
    }
}
