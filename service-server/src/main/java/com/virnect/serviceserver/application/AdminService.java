package com.virnect.serviceserver.application;

/*@Slf4j
@Service
@RequiredArgsConstructor*/
public class AdminService {

	/*private final SessionService sessionService;

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
			//.restrictedMode(companyRequest.isRestrictedMode())
			.videoRestrictedMode(companyRequest.isVideoRestrictedMode())
			.audioRestrictedMode(companyRequest.isAudioRestrictedMode())
			.build();

		LanguageRequest languageRequest = companyRequest.getLanguage();
		Language language = Language.builder()
			.transKoKr(languageRequest.isTransKoKr())
			.transEnUs(languageRequest.isTransEnUs())
			.transJaJp(languageRequest.isTransJaJp())
			.transZh(languageRequest.isTransZh())
			.transEsEs(languageRequest.isTransEsEs())
			.transFrFr(languageRequest.isTransFrFr())
			.transPlPl(languageRequest.isTransPlPl())
			.transRuRu(languageRequest.isTransRuRu())
			.transUkUa(languageRequest.isTransUkUa())
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
	}*/
}
