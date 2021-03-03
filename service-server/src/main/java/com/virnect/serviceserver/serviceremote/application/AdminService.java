package com.virnect.serviceserver.serviceremote.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.domain.Company;
import com.virnect.data.domain.Language;
import com.virnect.serviceserver.serviceremote.dto.request.company.CompanyRequest;
import com.virnect.serviceserver.serviceremote.dto.request.company.CompanyResponse;
import com.virnect.serviceserver.serviceremote.dto.request.room.LanguageRequest;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

	private final SessionTransactionalService sessionService;

	public ApiResponse<CompanyResponse> createCompany(CompanyRequest companyRequest) {

		ApiResponse<CompanyResponse> responseData;

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

		Language language = Language.builder()
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
			.build();

		company.setLanguage(language);

		try{
			if (sessionService.createCompany(company) != null) {
				CompanyResponse companyResponse = new CompanyResponse();
				companyResponse.setWorkspaceId(company.getWorkspaceId());
				companyResponse.setLicenseName(company.getLicenseName());
				companyResponse.setSessionType(company.getSessionType());
				responseData = new ApiResponse<>(companyResponse);
			} else {
				responseData = new ApiResponse<>(ErrorCode.ERR_COMPANY_CREATE_FAIL);
			}
		} catch (Exception e) {
			responseData = new ApiResponse<>(ErrorCode.ERR_COMPANY_CREATE_FAIL);
		}
		return responseData;
	}

	public ApiResponse<CompanyResponse> updateCompany(CompanyRequest companyRequest) {

		ApiResponse<CompanyResponse> responseData;

		Company company = sessionService.getCompany(companyRequest.getWorkspaceId());

		if (company == null) {
			responseData = new ApiResponse<>(ErrorCode.ERR_COMPANY_NOT_EXIST);
		} else {

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

			Language language = Language.builder()
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
				.build();

			company.setLanguage(language);

			try{
				if (sessionService.updateCompany(company) != null) {
					CompanyResponse companyResponse = new CompanyResponse();
					companyResponse.setWorkspaceId(company.getWorkspaceId());
					companyResponse.setLicenseName(company.getLicenseName());
					companyResponse.setSessionType(company.getSessionType());
					responseData = new ApiResponse<>(companyResponse);
				} else {
					responseData = new ApiResponse<>(ErrorCode.ERR_COMPANY_UPDATE_FAIL);
				}
			} catch (Exception e) {
				responseData = new ApiResponse<>(ErrorCode.ERR_COMPANY_UPDATE_FAIL);
			}
		}
		return responseData;
	}
}
