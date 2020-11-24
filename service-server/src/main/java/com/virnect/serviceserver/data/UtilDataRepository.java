package com.virnect.serviceserver.data;

import com.virnect.data.dao.Company;
import com.virnect.data.dao.Language;
import com.virnect.data.dao.SessionType;
import com.virnect.service.ApiResponse;
import com.virnect.service.constraint.CompanyConstants;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.constraint.TranslationItem;
import com.virnect.service.dto.LanguageCode;
import com.virnect.service.dto.service.request.CompanyRequest;
import com.virnect.service.dto.service.request.CompanyResponse;
import com.virnect.service.dto.service.request.LanguageRequest;
import com.virnect.service.dto.service.response.CompanyInfoResponse;
import com.virnect.service.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtilDataRepository extends DataRepository {
    private static final String TAG = UtilDataRepository.class.getSimpleName();

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

    public ApiResponse<CompanyResponse> generateCompany(CompanyRequest companyRequest) {
        return new RepoDecoder<Company, CompanyResponse>(RepoDecoderType.CREATE) {
            @Override
            Company loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<CompanyResponse> invokeDataProcess() {
                Company company = saveData(companyRequest);
                if(sessionService.createCompany(company) != null) {
                    CompanyResponse companyResponse = new CompanyResponse();
                    companyResponse.setWorkspaceId(company.getWorkspaceId());
                    companyResponse.setLicenseName(company.getLicenseName());
                    companyResponse.setSessionType(company.getSessionType());
                    return new DataProcess<>(companyResponse);
                } else {
                    return new DataProcess<>(ErrorCode.ERR_COMPANY_CREATE_FAIL);
                }
            }

            private Company saveData(CompanyRequest companyRequest) {
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

                return company;
            }
        }.asApiResponse();
    }

    /*public ApiResponse<CompanyInfoResponse> loadCompanyInformation(String workspaceId) {
        return new RepoDecoder<Company, CompanyInfoResponse>(RepoDecoderType.READ) {
            @Override
            Company loadFromDatabase() {
                return sessionService.getCompany(workspaceId);
            }

            @Override
            DataProcess<CompanyInfoResponse> invokeDataProcess() {
                Company company = loadFromDatabase();
                CompanyInfoResponse companyInfoResponse;
                if(company != null) {
                    companyInfoResponse = modelMapper.map(company, CompanyInfoResponse.class);
                    Language language = company.getLanguage();
                    List<LanguageCode> languageCodes = combineLanguageCode(language);
                    companyInfoResponse.setLanguageCodes(languageCodes);
                } else {
                    companyInfoResponse = new CompanyInfoResponse();
                }
                return new DataProcess<>(companyInfoResponse);
            }
        }.asApiResponse();
    }*/

    public ApiResponse<CompanyInfoResponse> loadCompanyInformation(String workspaceId) {
        return new RepoDecoder<Company, CompanyInfoResponse>(RepoDecoderType.READ) {
            @Override
            Company loadFromDatabase() {
                return sessionService.getCompany(workspaceId);
            }

            @Override
            DataProcess<CompanyInfoResponse> invokeDataProcess() {
                CompanyInfoResponse companyInfoResponse = defaultCompanyInfo(workspaceId);
                return new DataProcess<>(companyInfoResponse);
            }
        }.asApiResponse();
    }

    public ApiResponse<CompanyInfoResponse> loadCompanyInformation(int companyCode, String workspaceId, String userId) {
        return new RepoDecoder<Company, CompanyInfoResponse>(RepoDecoderType.READ) {

            @Override
            Company loadFromDatabase() {
                return sessionService.getCompany(workspaceId);
            }

            @Override
            DataProcess<CompanyInfoResponse> invokeDataProcess() {
                CompanyInfoResponse companyInfoResponse;
                if(companyCode != CompanyConstants.COMPANY_VIRNECT) {
                    Company company = loadFromDatabase();
                    if(company != null) {
                        companyInfoResponse = modelMapper.map(company, CompanyInfoResponse.class);
                        Language language = company.getLanguage();
                        List<LanguageCode> languageCodes = combineLanguageCode(language);
                        companyInfoResponse.setLanguageCodes(languageCodes);
                        return new DataProcess<>(companyInfoResponse);
                    } else {
                        CompanyInfoResponse empty = new CompanyInfoResponse();
                        return new DataProcess<>(empty, ErrorCode.ERR_COMPANY_INVALID_CODE);
                    }
                } else {
                    companyInfoResponse = defaultCompanyInfo(workspaceId);
                    return new DataProcess<>(companyInfoResponse);
                }
            }


        }.asApiResponse();
    }
}
