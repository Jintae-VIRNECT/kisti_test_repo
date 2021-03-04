package com.virnect.serviceserver.serviceremote.application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.company.CompanyRepository;
import com.virnect.data.domain.Company;
import com.virnect.data.domain.Language;
import com.virnect.data.domain.session.SessionType;
import com.virnect.serviceserver.serviceremote.dto.constraint.CompanyConstants;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseConstants;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseItem;
import com.virnect.serviceserver.serviceremote.dto.constraint.LanguageCode;
import com.virnect.serviceserver.serviceremote.dto.response.license.LicenseItemResponse;
import com.virnect.data.dto.rest.LicenseInfoListResponse;
import com.virnect.data.dto.rest.LicenseInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.JsonUtil;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.data.application.license.LicenseRestService;
import com.virnect.serviceserver.serviceremote.dto.constraint.TranslationItem;
import com.virnect.serviceserver.serviceremote.dto.response.company.CompanyInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

	private static final String TAG = ValidationService.class.getSimpleName();

	private final ModelMapper modelMapper;
	private final LicenseRestService licenseRestService;
	private final CompanyRepository companyRepository;

	public ApiResponse<LicenseItemResponse> getLicenseInfo(String workspaceId, String userId) {

		ApiResponse<LicenseItemResponse> responseData;

		ApiResponse<LicenseInfoListResponse> licenseValidation = this.licenseRestService.getUserLicenseValidation(
			workspaceId, userId);
		if (licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
			responseData = new ApiResponse<>(licenseValidation.getCode(), licenseValidation.getMessage());
		}

		LicenseInfoResponse currentLicense = null;
		for (LicenseInfoResponse licenseInfoResponse : licenseValidation.getData().getLicenseInfoList()) {
			if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
				currentLicense = licenseInfoResponse;
			}
		}

		LicenseItem licenseItem = LicenseItem.ITEM_PRODUCT;
		if (currentLicense == null) {
			responseData = new ApiResponse<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
		} else {
			if (!currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
				responseData = new ApiResponse<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
			} else {
				LicenseItemResponse licenseItemResponse = new LicenseItemResponse();
				licenseItemResponse.setItemName(licenseItem.getItemName());
				licenseItemResponse.setUserCapacity(licenseItem.getUserCapacity());
				responseData = new ApiResponse<>(licenseItemResponse);
			}
		}
		return responseData;
	}

	public ApiResponse<CompanyInfoResponse> getCompanyInfo(
		String workspaceId,
		String userId,
		String policyLocation
	) {
		ApiResponse<CompanyInfoResponse> responseData;

		Company company = companyRepository.findByWorkspaceId(workspaceId).orElse(null);
		CompanyInfoResponse companyInfoResponse = null;
		try {
			companyInfoResponse = loadServicePolicy(workspaceId, policyLocation);
		} catch (IOException e) {
			responseData = new ApiResponse<>(new CompanyInfoResponse(), ErrorCode.ERR_IO_EXCEPTION);
		}

		responseData = new ApiResponse<>(companyInfoResponse);

		return responseData;
	}

	private CompanyInfoResponse loadServicePolicy(String workspaceId, String policyLocation) throws IOException {
		//String policyLocation = config.remoteServiceProperties.getServicePolicyLocation();
		if (policyLocation == null || policyLocation.isEmpty()) {
			LogMessage.formedError(
				TAG,
				"initialise service policy",
				"loadServicePolicy",
				"service policy file path is null or empty. trying to set default service policy."
			);
			return defaultCompanyInfo(workspaceId);

		} else {
			JsonUtil jsonUtil = new JsonUtil();
			JsonObject jsonObject;
			if (policyLocation.startsWith("/")) {
				Path path = Paths.get(policyLocation);
				jsonObject = jsonUtil.fromFileToJsonObject(path.toAbsolutePath().toString());
			} else {
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(policyLocation);
				if (inputStream == null) {
					LogMessage.formedError(
						TAG,
						"initialise service policy",
						"loadServicePolicy",
						"service policy file path is null or empty. trying to set default service policy."
					);
					return defaultCompanyInfo(workspaceId);
				} else {
					LogMessage.formedError(
						TAG,
						"initialise service policy",
						"loadServicePolicy",
						"service policy file path is null or empty. set service policy using service policy file."
					);
					jsonObject = jsonUtil.fromInputStreamToJsonObject(inputStream);
					inputStream.close();

					return parseServicePolicyJsonObject(jsonObject, workspaceId);
				}
			}
			LogMessage.formedInfo(
				TAG,
				"initialise service policy",
				"loadServicePolicy",
				"load service policy is success."
			);
			return parseServicePolicyJsonObject(jsonObject, workspaceId);
		}
	}
	private CompanyInfoResponse defaultCompanyInfo(String workspaceId) {
		List<LanguageCode> languageCodes = new ArrayList<>(Arrays.asList(
			new LanguageCode(TranslationItem.LANGUAGE_KR),
			new LanguageCode(TranslationItem.LANGUAGE_EN),
			new LanguageCode(TranslationItem.LANGUAGE_JP),
			new LanguageCode(TranslationItem.LANGUAGE_ZH),
			new LanguageCode(TranslationItem.LANGUAGE_FR),
			new LanguageCode(TranslationItem.LANGUAGE_ES),
			new LanguageCode(TranslationItem.LANGUAGE_RU),
			new LanguageCode(TranslationItem.LANGUAGE_UK),
			new LanguageCode(TranslationItem.LANGUAGE_PL)
		));

		CompanyInfoResponse companyInfoResponse = new CompanyInfoResponse();
		companyInfoResponse.setCompanyCode(0);
		companyInfoResponse.setWorkspaceId(workspaceId);
		companyInfoResponse.setLicenseName(LicenseItem.ITEM_PRODUCT.getItemName());
		companyInfoResponse.setSessionType(SessionType.OPEN);
		companyInfoResponse.setRecording(true);
		companyInfoResponse.setStorage(true);
		companyInfoResponse.setTranslation(true);
		companyInfoResponse.setSttSync(true);
		companyInfoResponse.setSttStreaming(true);
		companyInfoResponse.setTts(true);
		companyInfoResponse.setLanguageCodes(languageCodes);

		return companyInfoResponse;
	}

	private CompanyInfoResponse parseServicePolicyJsonObject(JsonObject jsonObject, String workspaceId) {
		CompanyInfoResponse companyInfoResponse = new CompanyInfoResponse();
		JsonObject policyObject = jsonObject.getAsJsonObject("company_info");
		companyInfoResponse.setCompanyCode(policyObject.get("company_code").getAsInt());

		if (policyObject.get("workspace_id").getAsString().isEmpty()) {
			companyInfoResponse.setWorkspaceId(workspaceId);
		} else {
			companyInfoResponse.setWorkspaceId(policyObject.get("workspace_id").getAsString());
		}

		companyInfoResponse.setLicenseName(policyObject.get("license_name").getAsString());
		companyInfoResponse.setSessionType(SessionType.findBy(policyObject.get("session_type").getAsString()));
		companyInfoResponse.setRecording(policyObject.get("recording").getAsBoolean());
		companyInfoResponse.setStorage(policyObject.get("storage").getAsBoolean());
		companyInfoResponse.setTranslation(policyObject.get("translation").getAsBoolean());
		companyInfoResponse.setSttSync(policyObject.get("stt_sync").getAsBoolean());
		companyInfoResponse.setSttStreaming(policyObject.get("stt_streaming").getAsBoolean());
		companyInfoResponse.setTts(policyObject.get("tts").getAsBoolean());

		JsonArray jsonArray = policyObject.getAsJsonArray("language_codes");
		ArrayList<LanguageCode> languageCodes = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			LanguageCode languageCode = new LanguageCode(
				jsonArray.get(i).getAsJsonObject().get("text").getAsString(),
				jsonArray.get(i).getAsJsonObject().get("code").getAsString()
			);
			languageCodes.add(languageCode);
		}
		companyInfoResponse.setLanguageCodes(languageCodes);
		return companyInfoResponse;
	}

	public ApiResponse<CompanyInfoResponse> getCompanyInfoByCompanyCode(
		String workspaceId,
		String userId,
		int companyCode,
		String policyLocation
	) {

		ApiResponse<CompanyInfoResponse> responseData;

		CompanyInfoResponse companyInfoResponse = null;

		companyCode = 0;

		if (companyCode != CompanyConstants.COMPANY_VIRNECT) {
			Company company = companyRepository.findByWorkspaceId(workspaceId).orElse(null);
			if (company != null) {
				companyInfoResponse = modelMapper.map(company, CompanyInfoResponse.class);
				Language language = company.getLanguage();
				List<LanguageCode> languageCodes = combineLanguageCode(language);
				companyInfoResponse.setLanguageCodes(languageCodes);
				responseData = new ApiResponse<>(companyInfoResponse);
			} else {
				CompanyInfoResponse empty = new CompanyInfoResponse();
				responseData = new ApiResponse<>(empty, ErrorCode.ERR_COMPANY_INVALID_CODE);
			}
		} else {
			try {
				companyInfoResponse = loadServicePolicy(workspaceId, policyLocation);
			} catch (IOException e) {
				new ApiResponse<>(new CompanyInfoResponse(), ErrorCode.ERR_IO_EXCEPTION);
			}
			responseData = new ApiResponse<>(companyInfoResponse);
		}
		return responseData;
	}

	private List<LanguageCode> combineLanguageCode(Language language) {
		List<LanguageCode> languageCodes = new ArrayList<>();
		if (language.isTransKoKr()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_KR);
			languageCodes.add(languageCode);
		}
		if (language.isTransEnUs()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_EN);
			languageCodes.add(languageCode);
		}
		if (language.isTransJaJp()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_JP);
			languageCodes.add(languageCode);
		}
		if (language.isTransZh()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_ZH);
			languageCodes.add(languageCode);
		}
		if (language.isTransFrFr()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_FR);
			languageCodes.add(languageCode);
		}
		if (language.isTransEsEs()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_ES);
			languageCodes.add(languageCode);
		}
		if (language.isTransRuRu()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_RU);
			languageCodes.add(languageCode);
		}
		if (language.isTransUkUa()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_UK);
			languageCodes.add(languageCode);
		}
		if (language.isTransPlPl()) {
			LanguageCode languageCode = new LanguageCode(TranslationItem.LANGUAGE_PL);
			languageCodes.add(languageCode);
		}
		return languageCodes;
	}
}
