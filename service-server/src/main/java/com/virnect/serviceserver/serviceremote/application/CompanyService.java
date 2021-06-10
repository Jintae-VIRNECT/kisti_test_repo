package com.virnect.serviceserver.serviceremote.application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.company.CompanyRepository;
import com.virnect.data.domain.Company;
import com.virnect.data.domain.Language;
import com.virnect.data.domain.session.SessionType;
import com.virnect.data.dto.constraint.CompanyConstants;
import com.virnect.data.dto.constraint.LanguageCode;
import com.virnect.data.dto.constraint.LicenseItem;
import com.virnect.data.dto.constraint.TranslationItem;
import com.virnect.serviceserver.serviceremote.dto.mapper.company.CompanyMapper;
import com.virnect.data.dto.request.company.CompanyRequest;
import com.virnect.data.dto.request.company.CompanyResponse;
import com.virnect.data.dto.response.company.CompanyInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.JsonUtil;
import com.virnect.data.infra.utils.LogMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

	private static final String TAG = CompanyService.class.getSimpleName();

	//private final ModelMapper modelMapper;
	private final CompanyMapper companyMapper;

	private final SessionTransactionalService sessionService;
	private final CompanyRepository companyRepository;

	public ApiResponse<CompanyResponse> createCompany(CompanyRequest companyRequest) {
		try{
			Company company = Company.builder()
				.companyCode(companyRequest.getCompanyCode())
				.workspaceId(companyRequest.getWorkspaceId())
				.licenseName(companyRequest.getLicenseName())
				.sessionType(companyRequest.getSessionType())
				.recording(companyRequest.isRecording())
				.storage(companyRequest.isStorage())
				.translation(companyRequest.isTranslation())
				.sttStreaming(companyRequest.isSttStreaming())
				.sttSync(companyRequest.isSttSync())
				.tts(companyRequest.isTts())
				.videoRestrictedMode(companyRequest.isVideoRestrictedMode())
				.audioRestrictedMode(companyRequest.isAudioRestrictedMode())
				.localRecording(companyRequest.isLocalRecording())
				.build();

			company.setLanguage(
				Language.builder()
				.transKoKr(companyRequest.getLanguage().isTransKoKr())
				.transEnUs(companyRequest.getLanguage().isTransEnUs())
				.transJaJp(companyRequest.getLanguage().isTransJaJp())
				.transZh(companyRequest.getLanguage().isTransZh())
				.transEsEs(companyRequest.getLanguage().isTransEsEs())
				.transFrFr(companyRequest.getLanguage().isTransFrFr())
				.transPlPl(companyRequest.getLanguage().isTransPlPl())
				.transRuRu(companyRequest.getLanguage().isTransRuRu())
				.transUkUa(companyRequest.getLanguage().isTransUkUa())
				.company(company)
				.build()
			);

			if (sessionService.createCompany(company) == null) {
				return new ApiResponse<>(ErrorCode.ERR_COMPANY_CREATE_FAIL);
			}
			CompanyResponse companyResponse = new CompanyResponse();
			companyResponse.setWorkspaceId(company.getWorkspaceId());
			companyResponse.setLicenseName(company.getLicenseName());
			companyResponse.setSessionType(company.getSessionType());
			return new ApiResponse<>(companyResponse);
		} catch (Exception e) {
			return new ApiResponse<>(ErrorCode.ERR_COMPANY_CREATE_FAIL);
		}
	}

	public ApiResponse<CompanyResponse> updateCompany(CompanyRequest companyRequest) {

		Company company = sessionService.getCompany(companyRequest.getWorkspaceId());
		if (company == null) {
			return new ApiResponse<>(ErrorCode.ERR_COMPANY_NOT_EXIST);
		}

		try {
			company.setCompanyCode(companyRequest.getCompanyCode());
			company.setLicenseName(companyRequest.getLicenseName());
			company.setSessionType(companyRequest.getSessionType());
			company.setRecording(companyRequest.isRecording());
			company.setStorage(companyRequest.isStorage());
			company.setTranslation(companyRequest.isTranslation());
			company.setSttStreaming(companyRequest.isSttStreaming());
			company.setSttSync(companyRequest.isSttSync());
			company.setTts(companyRequest.isTts());
			company.setVideoRestrictedMode(companyRequest.isVideoRestrictedMode());
			company.setAudioRestrictedMode(companyRequest.isAudioRestrictedMode());
			company.setLocalRecording(companyRequest.isLocalRecording());

			company.setLanguage(
				Language.builder()
				.transKoKr(companyRequest.getLanguage().isTransKoKr())
				.transEnUs(companyRequest.getLanguage().isTransEnUs())
				.transJaJp(companyRequest.getLanguage().isTransJaJp())
				.transZh(companyRequest.getLanguage().isTransZh())
				.transEsEs(companyRequest.getLanguage().isTransEsEs())
				.transFrFr(companyRequest.getLanguage().isTransFrFr())
				.transPlPl(companyRequest.getLanguage().isTransPlPl())
				.transRuRu(companyRequest.getLanguage().isTransRuRu())
				.transUkUa(companyRequest.getLanguage().isTransUkUa())
				.company(company)
				.build()
			);

			if (sessionService.updateCompany(company) == null) {
				return new ApiResponse<>(ErrorCode.ERR_COMPANY_UPDATE_FAIL);
			}

			CompanyResponse companyResponse = new CompanyResponse();
			companyResponse.setWorkspaceId(company.getWorkspaceId());
			companyResponse.setLicenseName(company.getLicenseName());
			companyResponse.setSessionType(company.getSessionType());
			return new ApiResponse<>(companyResponse);

		} catch (Exception e) {
			return new ApiResponse<>(ErrorCode.ERR_COMPANY_UPDATE_FAIL);
		}
	}

	public ApiResponse<CompanyInfoResponse> getCompanyInfo(
		String workspaceId,
		String userId,
		String policyLocation
	) {
		Company company = companyRepository.findByWorkspaceId(workspaceId).orElse(null);
		CompanyInfoResponse companyInfoResponse = null;
		try {
			companyInfoResponse = loadServicePolicy(workspaceId, policyLocation);
		} catch (IOException e) {
			new ApiResponse<>(ErrorCode.ERR_IO_EXCEPTION);
		}
		return new ApiResponse<>(companyInfoResponse);
	}

	public ApiResponse<CompanyInfoResponse> getCompanyInfoByCompanyCode(
		String workspaceId,
		String userId,
		int companyCode,
		String policyLocation
	) {
		try {
			if (companyCode == CompanyConstants.COMPANY_VIRNECT) {
				CompanyInfoResponse companyInfoResponse = loadServicePolicy(workspaceId, policyLocation);
				return new ApiResponse<>(companyInfoResponse);
			}
		} catch (IOException e) {
			return new ApiResponse<>(ErrorCode.ERR_IO_EXCEPTION);
		}

		Company company = companyRepository.findByWorkspaceId(workspaceId).orElse(null);
		if (company == null) {
			CompanyInfoResponse empty = new CompanyInfoResponse();
			return new ApiResponse<>(ErrorCode.ERR_COMPANY_INVALID_CODE);
		}

		//CompanyInfoResponse companyInfoResponse = modelMapper.map(company, CompanyInfoResponse.class);
		CompanyInfoResponse companyInfoResponse = companyMapper.toDto(company);
		Language language = company.getLanguage();
		List<LanguageCode> languageCodes = combineLanguageCode(language);
		companyInfoResponse.setLanguageCodes(languageCodes);
		return new ApiResponse<>(companyInfoResponse);
	}

	private CompanyInfoResponse loadServicePolicy(String workspaceId, String policyLocation) throws IOException {
		if (StringUtils.isBlank(policyLocation)) {
			LogMessage.formedError(
				TAG,
				"initialise service policy",
				"loadServicePolicy",
				"service policy file path is null or empty. trying to set default service policy."
			);
			return defaultCompanyInfo(workspaceId);
		}

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
