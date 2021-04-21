package com.virnect.content.global.util;

import com.virnect.content.dto.PropertiesRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-04-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@SpringBootTest
@ActiveProfiles("test")
class PropertiesParsingHandlerTest {

    @Test
    void test(){
        //String prop = "{\"TargetID\":\"8ffea289-5bec-4f75-b25d-111cd3ff6231\",\"TargetSize\":10.0,\"PropertyInfo\":{\"f548eacc-ddb1-4518-8446-d1ab672cf29c\":{\"PropertyInfo\":{\"sceneGroupTitle\":\"씬 그룹 1\",\"sceneGroupDetail\":\"\",\"ComponentName\":\"씬 그룹 1\",\"ComponentType\":2,\"identifier\":\"f548eacc-ddb1-4518-8446-d1ab672cf29c\"},\"Child\":{\"310377d1-228b-4120-82af-43eab4d052c1\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬 1\",\"ComponentType\":3,\"identifier\":\"310377d1-228b-4120-82af-43eab4d052c1\"}}}}}}";
        //String prop ="{\"TargetID\":\"8ffea289-5bec-4f75-b25d-111cd3ff6231\",\"TargetSize\":10.0}";
        //String prop ="{\"TargetID\":\"8ffea289-5bec-4f75-b25d-111cd3ff6231\",\"TargetSize\":10.0,\"PropertyInfo\":{\"f548eacc-ddb1-4518-8446-d1ab672cf29c\":{}}}";
        String prop = "{\"TargetID\":\"8ffea289-5bec-4f75-b25d-111cd3ff6231\",\"TargetSize\":10.0,\"PropertyInfo\":{\"f548eacc-ddb1-4518-8446-d1ab672cf29c\":{\"PropertyInfo\":{\"sceneGroupTitle\":\"씬 그룹 1\",\"sceneGroupDetail\":\"\",\"ComponentName\":\"씬 그룹 1\",\"ComponentType\":2,\"identifier\":\"f548eacc-ddb1-4518-8446-d1ab672cf29c\"},\"Child\":{\"310377d1-228b-4120-82af-43eab4d052c1\":{\"PropertyInfo\":{}}}}}}";
        PropertiesRequest propertiesRequest = new PropertiesParsingHandler().getPropertiesRequest(prop);
        System.out.println(propertiesRequest.toString());
    }
}