package com.virnect.serviceserver.test.unit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.virnect.data.domain.session.SessionType;
import com.virnect.data.dto.response.company.CompanyInfoResponse;
import com.virnect.data.dto.constraint.LanguageCode;
import com.virnect.serviceserver.infra.utils.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//@TestPropertySource(locations = "classpath:application.properties")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ServicePolicyTest {

    @Test
    void loadServicePolicyTest() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("policy/servicePolicy.json");
        JsonUtil jsonUtil = new JsonUtil();
        JsonObject jsonObject = jsonUtil.fromInputStreamToJsonObject(inputStream);

        assert inputStream != null;
        inputStream.close();

        CompanyInfoResponse companyInfoResponse = new CompanyInfoResponse();
        JsonObject policyObject = jsonObject.getAsJsonObject("company_info");
        companyInfoResponse.setCompanyCode(policyObject.get("company_code").getAsInt());
        companyInfoResponse.setWorkspaceId(policyObject.get("workspace_id").getAsString());
        companyInfoResponse.setLicenseName(policyObject.get("license_name").getAsString());
        companyInfoResponse.setSessionType(SessionType.findBy(policyObject.get("session_type").getAsString()));
        companyInfoResponse.setRecording(policyObject.get("recording").getAsBoolean());
        companyInfoResponse.setStorage(policyObject.get("storage").getAsBoolean());
        companyInfoResponse.setTranslation(policyObject.get("translation").getAsBoolean());
        companyInfoResponse.setSttSync(policyObject.get("stt_sync").getAsBoolean());
        companyInfoResponse.setSttStreaming(policyObject.get("stt_streaming").getAsBoolean());
        companyInfoResponse.setTts(policyObject.get("tts").getAsBoolean());

        System.out.println(companyInfoResponse.toString());

        JsonArray jsonArray = policyObject.getAsJsonArray("language_codes");
        ArrayList<LanguageCode> languageCodes = new ArrayList<>();
        for(int i = 0; i<jsonArray.size(); i++) {
            LanguageCode languageCode = new LanguageCode(
                    jsonArray.get(i).getAsJsonObject().get("text").getAsString(),
                    jsonArray.get(i).getAsJsonObject().get("code").getAsString()
            );
            languageCodes.add(languageCode);
        }
        companyInfoResponse.setLanguageCodes(languageCodes);
        for (LanguageCode code: companyInfoResponse.getLanguageCodes()) {
            System.out.println(code.toString());
        }
    }

    @Test
    void loadStoragePolicyTest() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("policy/storagePolicy.json");
        JsonUtil jsonUtil = new JsonUtil();
        JsonObject jsonObject = jsonUtil.fromInputStreamToJsonObject(inputStream);

        assert inputStream != null;
        inputStream.close();

        System.out.println(jsonObject.toString());
    }
}
