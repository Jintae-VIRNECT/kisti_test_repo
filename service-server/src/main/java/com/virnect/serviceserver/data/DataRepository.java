package com.virnect.serviceserver.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.data.ApiResponse;
import com.virnect.data.constraint.LicenseConstants;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.constraint.TranslationItem;
import com.virnect.data.dao.*;
import com.virnect.data.dto.*;
import com.virnect.data.dto.feign.*;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.*;
import com.virnect.data.dto.rpc.ClientMetaData;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.service.HistoryService;
import com.virnect.data.service.SessionService;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.core.Participant;
import com.virnect.serviceserver.feign.service.LicenseRestService;
import com.virnect.serviceserver.feign.service.UserRestService;
import com.virnect.serviceserver.feign.service.WorkspaceRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataRepository {
    private static final String TAG = DataRepository.class.getSimpleName();

    private final RemoteServiceConfig config;
    private SessionService sessionService;
    private final HistoryService historyService;
    private final WorkspaceRestService workspaceRestService;
    private final UserRestService userRestService;
    private final LicenseRestService licenseRestService;
    private final ModelMapper modelMapper;
    //
    //private final LocalFileManagementService localFileManagementService;

    //private final ImplementationTest implementationTest;
    @Autowired(required = true)
    @Qualifier(value = "sessionService")
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }



    public DataProcess<UserInfoResponse> checkUserValidation(String userId) {
        return new RepoDecoder<ApiResponse<UserInfoResponse>, UserInfoResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<UserInfoResponse> loadFromDatabase() {
                return userRestService.getUserInfoByUserId(userId);
            }

            @Override
            DataProcess<UserInfoResponse> invokeDataProcess() {
                ApiResponse<UserInfoResponse> feignResponse = loadFromDatabase();
                //todo:check something?

                return new DataProcess<>(feignResponse.getData());
            }
        }.asResponseData();
    }

    public DataProcess<LicenseItem> checkLicenseValidation(String workspaceId, String userId) {
        return new RepoDecoder<DataProcess<LicenseInfoResponse>, LicenseItem>(RepoDecoderType.FETCH) {
            @Override
            DataProcess<LicenseInfoResponse> loadFromDatabase() {
                ApiResponse<LicenseInfoListResponse> licenseValidation = licenseRestService.getUserLicenseValidation(workspaceId, userId);
                if(licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("licenseValidation code is not ok");
                    return new DataProcess<>(licenseValidation.getCode(), licenseValidation.getMessage());
                }

                LicenseInfoResponse currentLicense = null;
                for (LicenseInfoResponse licenseInfoResponse: licenseValidation.getData().getLicenseInfoList()) {
                    if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
                        currentLicense = licenseInfoResponse;
                    }
                }
                return new DataProcess<>(currentLicense);
            }

            @Override
            DataProcess<LicenseItem> invokeDataProcess() {
                LicenseItem licenseItem = LicenseItem.ITEM_PRODUCT;
                LicenseInfoResponse currentLicense = loadFromDatabase().getData();
                if(currentLicense == null) {
                    return new DataProcess<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
                } else {
                    if (!currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
                        return new DataProcess<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
                    }
                    return new DataProcess<>(licenseItem);
                }
            }
        }.asResponseData();
    }

    public ApiResponse<CompanyResponse> generateCompany(CompanyRequest companyRequest) {
        return new RepoDecoder<Company, CompanyResponse>(RepoDecoderType.CREATE) {
            @Override
            Company loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<CompanyResponse> invokeDataProcess() {
                Company company = sessionService.createCompany(companyRequest);

                CompanyResponse companyResponse = new CompanyResponse();
                companyResponse.setWorkspaceId(company.getWorkspaceId());
                companyResponse.setLicenseName(company.getLicenseName());
                companyResponse.setSessionType(company.getSessionType());

                return new DataProcess<>(companyResponse);

            }
        }.asApiResponse();
    }

    public ApiResponse<CompanyInfoResponse> loadCompanyInformation(String workspaceId) {
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
                    //
                    if(company.isTransKoKr()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_KR.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_KR.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransEnUs()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_EN.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_EN.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransJaJp()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_JP.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_JP.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransZh()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_ZH.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_ZH.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransFrFr()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_FR.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_FR.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransEsEs()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_ES.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_ES.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransRuRu()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_RU.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_RU.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransUkUa()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_UK.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_UK.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                    if(company.isTransPlPl()) {
                        LanguageCode languageCode = new LanguageCode();
                        languageCode.setText(TranslationItem.LANGUAGE_PL.getLanguage());
                        languageCode.setCode(TranslationItem.LANGUAGE_PL.getLanguageCode());
                        companyInfoResponse.getLanguageCodes().add(languageCode);
                    }
                } else {
                    companyInfoResponse = new CompanyInfoResponse();
                }
                return new DataProcess<>(companyInfoResponse);
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomResponse> generateRoom(
            RoomRequest roomRequest,
            LicenseItem licenseItem,
            String session,
            String sessionToken
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        SessionResponse sessionResponse = null;
        try {
            sessionResponse = objectMapper.readValue(session, SessionResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        SessionTokenResponse sessionTokenResponse = null;
        try {
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert sessionResponse != null;
        assert sessionTokenResponse != null;

        SessionResponse finalSessionResponse = sessionResponse;
        SessionTokenResponse finalSessionTokenResponse = sessionTokenResponse;
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.CREATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                log.info("createRoom: " + roomRequest.toString());
                /*ApiResponse<LicenseInfoListResponse> licenseValidation = licenseRestService.getUserLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
                if(licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("licenseValidation code is not ok");
                    return new DataProcess<>(licenseValidation.getCode(), licenseValidation.getMessage());
                }

                LicenseInfoResponse currentLicense = null;
                for (LicenseInfoResponse licenseInfoResponse: licenseValidation.getData().getLicenseInfoList()) {
                    if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
                        currentLicense = licenseInfoResponse;
                    }
                }

                LicenseItem licenseItem = null;
                if(currentLicense != null) {
                    if (currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
                        if (currentLicense.getLicenseType().contains(LicenseConstants.LICENSE_BASIC)) {
                            licenseItem = LicenseItem.ITEM_BASIC;
                        }
                        if (currentLicense.getLicenseType().contains(LicenseConstants.LICENSE_BUSINESS)) {
                            licenseItem = LicenseItem.ITEM_BUSINESS;
                        }
                        if (currentLicense.getLicenseType().contains(LicenseConstants.LICENSE_PERMANENT)) {
                            licenseItem = LicenseItem.ITEM_PERMANENT;
                        }
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
                }

                if(licenseItem == null) {
                    return new DataProcess<>(ErrorCode.ERR_LICENSE_TYPE_VALIDITY);
                }*/

                Room room = sessionService.createRoom(roomRequest, licenseItem, finalSessionResponse);
                if(room != null) {
                    RoomResponse roomResponse = new RoomResponse();
                    //not set session create at property
                    roomResponse.setSessionId(finalSessionResponse.getId());
                    roomResponse.setToken(finalSessionTokenResponse.getToken());
                    roomResponse.setWss(ServiceServerApplication.wssUrl);
                    for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisList()) {
                        CoturnResponse coturnResponse = new CoturnResponse();
                        coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                        coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                        coturnResponse.setUrl(coturnUrl);
                        roomResponse.getCoturn().add(coturnResponse);
                    }
                    return new DataProcess<>(roomResponse);
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
                }
            }
        }.asApiResponse();
    }

    public DataProcess<Boolean> generateRoomSession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                log.info("GENERATE ROOM SESSION RETRIEVE BY SESSION ID => [{}]", sessionId);
                sessionService.createSession(sessionId);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public ApiResponse<RoomInfoListResponse> loadRoomList(
            String workspaceId,
            String userId,
            boolean paging,
            Pageable pageable
    ) {
        return new RepoDecoder<Member, RoomInfoListResponse>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomInfoListResponse> invokeDataProcess() {
                if(!paging) {
                    log.info("INVOKE METHOD : loadRoomList :: not paging {} {}", workspaceId, userId);
                    //first add private room
                    List<Room> roomList = sessionService.getRoomList(workspaceId, userId);
                    //send add open room
                    roomList.addAll(sessionService.getRoomList(workspaceId));

                    List<RoomInfoResponse> roomInfoList = new ArrayList<>();
                    for (Room room: roomList) {
                        log.info("INVOKE METHOD : roomList ::");
                        RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
                        roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());
                        roomInfoList.add(roomInfoResponse);
                    }

                    /*List<RoomInfoResponse> roomInfoList = sessionService.getRoomList(workspaceId, userId)
                            .stream()
                            .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // Page Metadata Empty
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(0)
                            .currentSize(0)
                            .totalPage(0)
                            .totalElements(0)
                            .build();

                    // Get Member List by Room Session Ids
                    for (RoomInfoResponse response: roomInfoList) {
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(workspaceId, response.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from workspace-server using uuid
                        if(!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                                log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                                //todo://user infomation does not have role and role id change to workspace member info
                                WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                                memberInfoResponse.setRole(workspaceMemberData.getRole());
                                //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                                memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                                memberInfoResponse.setName(workspaceMemberData.getName());
                                memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                                memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                            }
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                        //log.debug("getRoomInfoList: {}", response.toString());
                    }
                    return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
                } else {
                    log.info("INVOKE METHOD : loadRoomList :: paging");

                    Page<Member> memberPage = sessionService.getMemberList(workspaceId, userId, pageable);

                    //first add private room
                    List<Room> roomList = sessionService.getRoomList(memberPage);
                    //send add open room
                    roomList.addAll(sessionService.getRoomList(workspaceId));

                    List<RoomInfoResponse> roomInfoList = new ArrayList<>();
                    for (Room room: roomList) {
                        RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
                        roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());
                        roomInfoList.add(roomInfoResponse);
                    }

                    /*List<RoomInfoResponse> roomInfoList = sessionService.getRoomList(memberPage)
                            .stream()
                            .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(pageable.getPageNumber())
                            .currentSize(pageable.getPageSize())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getNumberOfElements())
                            .build();

                    //roomInfoList.forEach(info -> log.info("{}", info));
                    log.info("Paging Metadata: {}", pageMeta.toString());

                    // Get Member List by Room Session Ids
                    for (RoomInfoResponse response: roomInfoList) {
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(response.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());
                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from workspace-server using uuid
                        if(!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                                log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                                //todo://user infomation does not have role and role id change to workspace member info
                                WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                                memberInfoResponse.setRole(workspaceMemberData.getRole());
                                //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                                memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                                memberInfoResponse.setName(workspaceMemberData.getName());
                                memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                                memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                            }
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                    }
                    return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> loadRoom(String workspaceId, String sessionId) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                //ApiResponse<RoomDetailInfoResponse> response = new ApiResponse<>();
                Room room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else {
                    if (room.getRoomStatus() != RoomStatus.ACTIVE) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
                    } else {
                        RoomDetailInfoResponse resultResponse;
                        // mapping data
                        //RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        resultResponse.setSessionType(room.getSessionProperty().getSessionType());

                        // Get Member List by Room Session ID
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(resultResponse.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        //remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from workspace-server using uuid
                        //if (!memberInfoList.isEmpty()) {
                        for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                            ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                            log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                            //todo://user infomation does not have role and role id change to workspace member info
                            WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                            memberInfoResponse.setRole(workspaceMemberData.getRole());
                            //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                            memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                            memberInfoResponse.setName(workspaceMemberData.getName());
                            memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                            memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                        }
                        //}
                        // Set Member List to Room Detail Information Response
                        resultResponse.setMemberList(memberInfoList);
                        return new DataProcess<>(resultResponse);
                    }
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDeleteResponse> removeRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Room, RoomDeleteResponse>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", sessionId);
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDeleteResponse> invokeDataProcess() {
                Room room = loadFromDatabase();
                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", room.getMembers().size());
                DataProcess<RoomDeleteResponse> dataProcess = null;
                try {
                    dataProcess = new DataProcess<>(RoomDeleteResponse.class);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                log.info("ROOM INFO DELETE BY dataProcess => [{}]", dataProcess.data.toString());

                if(room == null) {
                    dataProcess.setErrorCode(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return dataProcess;
                }

                //check request user has valid permission
                if(!room.getLeaderId().equals(userId)) {
                    dataProcess.setErrorCode(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    return dataProcess;
                }

                //List<Member> members = room.getMembers();
                List<Member> members = sessionService.getMemberList(room.getWorkspaceId(), room.getSessionId());
                for (Member member: members) {
                    log.info("ROOM INFO DELETE BY dataProcess => [{}]", member.getUuid());
                    if(member.getUuid().equals(room.getLeaderId())
                            && member.getMemberStatus().equals(MemberStatus.LOAD)) {
                        dataProcess.setErrorCode(ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED);
                        return dataProcess;
                    }
                }

                sessionService.removeRoom(room);
                //sessionService.removeRoom(workspaceId, sessionId);
                return new DataProcess<>(new RoomDeleteResponse(
                        sessionId,
                        true,
                        LocalDateTime.now()
                ));
            }
        }.asApiResponse();
    }

    /*public ApiResponse<RoomProfileUpdateResponse> updateRoom(
            String workspaceId,
            String sessionId,
            RoomProfileUpdateRequest roomProfileUpdateRequest) {
        return new RepoDecoder<Room, RoomProfileUpdateResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomProfileUpdateResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE PROFILE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                RoomProfileUpdateResponse profileUpdateResponse = new RoomProfileUpdateResponse();
                String profile = Default.ROOM_PROFILE.getValue();

                Room room = loadFromDatabase();
                if(room != null) {
                    if(room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
                        if (roomProfileUpdateRequest.getProfile() != null) {
                            try {
                                profile = localFileManagementService.upload(roomProfileUpdateRequest.getProfile());
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                        profileUpdateResponse.setSessionId(sessionId);
                        profileUpdateResponse.setProfile(profile);
                        sessionService.updateRoom(room, profile);
                        return new DataProcess<>(profileUpdateResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }*/

    public ApiResponse<RoomDetailInfoResponse> updateRoom(
            String workspaceId,
            String sessionId,
            ModifyRoomInfoRequest modifyRoomInfoRequest) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                Room room = loadFromDatabase();
                room = sessionService.updateRoom(room, modifyRoomInfoRequest);
                if(room != null) {
                    if(room.getLeaderId().equals(modifyRoomInfoRequest.getUuid())) {
                        // mapping data
                        RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        resultResponse.setSessionType(room.getSessionProperty().getSessionType());
                        // Get Member List by Room Session ID
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(resultResponse.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                                log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                                //todo://user infomation does not have role and role id change to workspace member info
                                WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                                memberInfoResponse.setRole(workspaceMemberData.getRole());
                                //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                                memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                                memberInfoResponse.setName(workspaceMemberData.getName());
                                memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                                memberInfoResponse.setProfile(workspaceMemberData.getProfile());

                                /*ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                                log.debug("getUsers: " + userInfo.getData().toString());

                                memberInfoResponse.setEmail(userInfo.getData().getEmail());
                                memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                                memberInfoResponse.setLastName(userInfo.getData().getLastName());
                                memberInfoResponse.setNickname(userInfo.getData().getNickname());
                                memberInfoResponse.setProfile(userInfo.getData().getProfile());*/
                            }
                        }
                        // Set Member List to Room Detail Information Response
                        resultResponse.setMemberList(memberInfoList);
                        return new DataProcess<>(resultResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }

    /**
     * Prepare to join the room the user is....
     * @param workspaceId
     * @param sessionId
     * @param userId
     * @return
     */
    public DataProcess<Boolean> prepareJoinRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                Room room = loadFromDatabase();
                if (room == null) {
                    return new DataProcess<>(false, ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                if (room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(userId)) {
                            log.info("open room has member Id is {}", member.getUuid());
                            if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                                return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                            }
                        }
                    }
                    log.info("open room has no member Id is {}", userId);
                    return new DataProcess<>(true);
                } else {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(userId)) {
                            log.info("private room has member Id is {}", member.getUuid());
                            if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                                return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                            } else {
                                return new DataProcess<>(true);
                            }
                        }
                    }
                    return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                }
            }
        }.asResponseData();
    }

    public ApiResponse<RoomResponse> joinRoom(String workspaceId, String sessionId, String sessionToken, JoinRoomRequest joinRoomRequest) {
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                //ApiResponse<RoomResponse> response = new ApiResponse<>();
                /*Room room = loadFromDatabase();
                if (room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    *//*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;*//*
                }*/
                Room room = loadFromDatabase();
                sessionService.joinRoom(room, joinRoomRequest);

                SessionTokenResponse sessionTokenResponse = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                assert sessionTokenResponse != null;

                RoomResponse roomResponse = new RoomResponse();
                //not set session create at property
                roomResponse.setSessionId(sessionId);
                roomResponse.setToken(sessionTokenResponse.getToken());

                roomResponse.setWss(ServiceServerApplication.wssUrl);
                for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisList()) {
                    CoturnResponse coturnResponse = new CoturnResponse();
                    coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                    coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                    coturnResponse.setUrl(coturnUrl);
                    roomResponse.getCoturn().add(coturnResponse);
                }
                return new DataProcess<>(roomResponse);

                /*ErrorCode errorCode = sessionService.joinRoom(room, joinRoomRequest);
                switch (errorCode) {
                    case ERR_SUCCESS: {
                        SessionTokenResponse sessionTokenResponse = null;
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        assert sessionTokenResponse != null;

                        RoomResponse roomResponse = new RoomResponse();
                        //not set session create at property
                        roomResponse.setSessionId(sessionId);
                        roomResponse.setToken(sessionTokenResponse.getToken());

                        roomResponse.setWss(ServiceServerApplication.wssUrl);
                        for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisList()) {
                            CoturnResponse coturnResponse = new CoturnResponse();
                            coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                            coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                            coturnResponse.setUrl(coturnUrl);
                            roomResponse.getCoturn().add(coturnResponse);
                        }
                        return new DataProcess<>(roomResponse);
                    }
                    case ERR_ROOM_MEMBER_ALREADY_JOINED: {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                        //response.setErrorResponseData(errorCode);
                        //return response;
                    }
                    default: {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                        //return response;
                    }
                }*/
            }
        }.asApiResponse();
    }

    public DataProcess<Boolean> joinSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ClientMetaData clientMetaData = null;
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session join and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session join and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session join and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                sessionService.joinSession(sessionId, participant.getParticipantPublicId(), clientMetaData);
                return new DataProcess<>(true);

                /*if(!Objects.equals(clientMetaData.getRoleType(), MemberType.LEADER.name())
                || !Objects.equals(clientMetaData.getRoleType(), MemberType.EXPERT.name())
                || !Objects.equals(clientMetaData.getRoleType(), MemberType.WORKER.name())
                ) {
                    return new DataProcess<>(false);
                } else {

                }*/
            }
        }.asResponseData();
    }

    public ApiResponse<ResultResponse> exitRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, userId);

                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else if(member == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                } else {
                    if(room.getMembers().isEmpty()) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                    } else {
                        ErrorCode errorCode = sessionService.exitRoom(room, member);
                        if(errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                            ResultResponse resultResponse = new ResultResponse();
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                        } else {
                            return new DataProcess<>(errorCode);
                        }
                    }
                }
            }
        }.asApiResponse();
    }

    public DataProcess<Boolean> leaveSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ClientMetaData clientMetaData = null;
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                sessionService.leaveSession(sessionId, clientMetaData);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> disconnectSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ClientMetaData clientMetaData = null;
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session disconnect and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session disconnect and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session disconnect and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                sessionService.disconnectSession(sessionId, clientMetaData);
                return new DataProcess<>(true);
            }
        }.asResponseData();

    }

    public DataProcess<Boolean> destroySession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                sessionService.destroySession(sessionId);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public DataProcess<String> evictParticipant(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Member, String>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return sessionService.getMember(workspaceId, sessionId, userId);
            }

            @Override
            DataProcess<String> invokeDataProcess() {
                Member member = loadFromDatabase();
                if(member != null) {
                    return new DataProcess<>(member.getConnectionId());
                }
                return new DataProcess<>("");
            }
        }.asResponseData();
    }

    public DataProcess<List<String>> getConnectionIds(String workspaceId, String sessionId) {
        return new RepoDecoder<Room, List<String>>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<List<String>> invokeDataProcess() {
                Room room = loadFromDatabase();
                List<Member> memberList = room.getMembers();

                //Do not use collection method removeIf with Data Access Object
                //memberList.removeIf(member -> member.getUuid().equals(room.getLeaderId()));

                List<String> connectionIds = new ArrayList<>();
                for (Member member: memberList) {
                    if(member.getConnectionId() != null &&
                            !member.getUuid().equals(room.getLeaderId())) {
                        connectionIds.add(member.getConnectionId());
                    }
                }
                return new DataProcess<>(connectionIds);
            }
        }.asResponseData();

    }

    public ApiResponse<ResultResponse> kickFromRoom(String workspaceId, String sessionId, KickRoomRequest kickRoomRequest) {
        return new RepoDecoder<Member, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Member loadFromDatabase() {
                return sessionService.getMember(workspaceId, sessionId, kickRoomRequest.getParticipantId());
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                ResultResponse resultResponse = new ResultResponse();
                resultResponse.setResult(false);
                //DataProcess<ResultResponse> response = new DataProcess<>(resultResponse);
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = loadFromDatabase();
                //Member member = sessionService.getMember(workspaceId, sessionId, kickRoomRequest.getParticipantId());
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    //response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else if(member == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                } else {
                    if (room.getMembers().isEmpty()) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                    } else if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    } else {
                        sessionService.updateMember(member, MemberStatus.EVICTED);
                        if(sessionService.removeMember(room, kickRoomRequest.getParticipantId()).equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                            //resultResponse.setResult(true);
                        } else {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_PROCESS_FAIL);
                        }
                    }
                }
                //return new DataProcess<>(resultResponse);
                //return response;
            }
        }.asApiResponse();
    }

    public ApiResponse<InviteRoomResponse> inviteMember(String workspaceId, String sessionId, InviteRoomRequest inviteRoomRequest) {
        return new RepoDecoder<Room, InviteRoomResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<InviteRoomResponse> invokeDataProcess() {
                log.info("ROOM INVITE MEMBER UPDATE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                Room room = loadFromDatabase();
                if(room != null) {
                    if(room.getLeaderId().equals(inviteRoomRequest.getLeaderId())) {
                        //remove if member status is evicted
                        //room.getMembers().removeIf(member -> member.getRoom() == null);
                        room.getMembers().removeIf(member -> member.getMemberStatus().equals(MemberStatus.EVICTED));

                        //check room member is exceeded limitation
                        if(room.getMembers().size() + inviteRoomRequest.getParticipantIds().size() > room.getMaxUserCount()) {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
                        }

                        //check invited member is already joined
                        for(String participant : inviteRoomRequest.getParticipantIds()) {
                            for(Member member: room.getMembers()) {
                                if(participant.equals(member.getUuid()) && !member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                                }
                            }
                        }

                        //update room member using iterator avoid to ConcurrentModificationException? ...
                        sessionService.updateMember(room, inviteRoomRequest.getParticipantIds());

                        InviteRoomResponse response = new InviteRoomResponse();
                        response.setWorkspaceId(workspaceId);
                        response.setSessionId(sessionId);
                        response.setLeaderId(inviteRoomRequest.getLeaderId());
                        response.setParticipantIds(inviteRoomRequest.getParticipantIds());
                        response.setTitle(room.getTitle());

                        return new DataProcess<>(response);

                        //sessionService.updateRoom(room, inviteRoomRequest.getParticipantIds());
                        /*for(String participant : inviteRoomRequest.getParticipantIds()) {
                            sessionService.createOrUpdateMember(room, participant);
                        }*/
                        /*List<String> additionalIds = new ArrayList<>();
                        for(String participant : inviteRoomRequest.getParticipantIds()) {
                            for(Member member: room.getMembers()) {
                                if(participant.equals(member.getUuid()) && member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                                    additionalIds.add();
                                } else {
                                    additionalIds.add();
                                }
                            }
                            sessionService.createOrUpdateMember(room, participant);
                        }*/

                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }


    //========================================= MEMBER INFORMATION RELATION =================================================//
    public ApiResponse<WorkspaceMemberInfoListResponse> loadMemberList(String workspaceId, String filter, int page, int size) {
        return new RepoDecoder<ApiResponse<WorkspaceMemberInfoListResponse>, WorkspaceMemberInfoListResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<WorkspaceMemberInfoListResponse> loadFromDatabase() {
                log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
                return workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, page, size);
            }

            @Override
            DataProcess<WorkspaceMemberInfoListResponse> invokeDataProcess() {
                ApiResponse<WorkspaceMemberInfoListResponse> feignResponse = loadFromDatabase();
                List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse.getData().getMemberInfoList();
                int currentPage = feignResponse.getData().getPageMeta().getCurrentPage();
                int totalPage = feignResponse.getData().getPageMeta().getTotalPage();

                // set Page Metadata
                feignResponse.getData().getPageMeta().setNumberOfElements(workspaceMemberInfoList.size());
                feignResponse.getData().getPageMeta().setLast(currentPage >= totalPage);

                return new DataProcess<>(feignResponse.getData());
            }
        }.asApiResponse();
    }

    public ApiResponse<MemberInfoListResponse> loadMemberList(String workspaceId, String userId, String filter, int page, int size) {
        return new RepoDecoder<ApiResponse<WorkspaceMemberInfoListResponse>, MemberInfoListResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<WorkspaceMemberInfoListResponse> loadFromDatabase() {
                log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
                return workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, page, size);
            }

            @Override
            DataProcess<MemberInfoListResponse> invokeDataProcess() {
                ApiResponse<WorkspaceMemberInfoListResponse> feignResponse = loadFromDatabase();
                List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse.getData().getMemberInfoList();
                PageMetadataResponse workspaceMemberPageMeta = feignResponse.getData().getPageMeta();
                int currentPage = workspaceMemberPageMeta.getCurrentPage();
                int currentSize = workspaceMemberPageMeta.getCurrentSize();
                int totalPage = workspaceMemberPageMeta.getTotalPage();
                long totalElements = workspaceMemberPageMeta.getTotalElements();

                //remove members who does not have any license plan or remote license
                workspaceMemberInfoList.removeIf(memberInfoResponses ->
                        Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
                                !Arrays.toString(memberInfoResponses.getLicenseProducts()).contains(LicenseConstants.PRODUCT_NAME));

                //remove member who has the same user id(::uuid)
                workspaceMemberInfoList.removeIf(memberInfoResponses -> memberInfoResponses.getUuid().equals(userId));

                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(currentPage)
                        .currentSize(currentSize)
                        .totalPage(totalPage)
                        .totalElements(totalElements)
                        .numberOfElements(workspaceMemberInfoList.size())
                        .build();

                // set page meta data last field to true or false
                pageMeta.setLast(currentPage >= totalPage);

                List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
                        .map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                return new DataProcess<>(new MemberInfoListResponse(memberInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<MemberInfoListResponse> loadMemberList(String workspaceId, String sessionId, String userId, String filter, int page, int size) {
        return new RepoDecoder<ApiResponse<WorkspaceMemberInfoListResponse>, MemberInfoListResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<WorkspaceMemberInfoListResponse> loadFromDatabase() {
                log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
                return workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, page, size);
            }

            @Override
            DataProcess<MemberInfoListResponse> invokeDataProcess() {
                Room room = sessionService.getRoom(workspaceId, sessionId);
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else {
                    // Get Member List from Room
                    // Mapping Member List Data to Member Information List
                    List<Member> memberList = room.getMembers();
                    //remove members who does not have room id
                    memberList.removeIf(member -> member.getRoom() == null);

                    //fetch workspace member information from workspace
                    ApiResponse<WorkspaceMemberInfoListResponse> feignResponse = loadFromDatabase();
                    List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse.getData().getMemberInfoList();
                    PageMetadataResponse workspaceMemberPageMeta = feignResponse.getData().getPageMeta();
                    int currentPage = workspaceMemberPageMeta.getCurrentPage();
                    int currentSize = workspaceMemberPageMeta.getCurrentSize();
                    int totalPage = workspaceMemberPageMeta.getTotalPage();
                    long totalElements = workspaceMemberPageMeta.getTotalElements();

                    //remove members who does not have any license plan or remote license
                    workspaceMemberInfoList.removeIf(memberInfoResponses ->
                            Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
                                    !Arrays.toString(memberInfoResponses.getLicenseProducts()).contains(LicenseConstants.PRODUCT_NAME));

                    //remove member who has the same user id(::uuid)
                    //do not remove member who has status evicted;
                    //workspaceMemberInfoList.removeIf(memberInfoResponses -> memberInfoResponses.getUuid().equals(userId));
                    memberList.forEach(member -> {
                        workspaceMemberInfoList.removeIf(memberInfoResponses ->
                                member.getMemberStatus() != MemberStatus.EVICTED &&
                                memberInfoResponses.getUuid().equals(member.getUuid())
                        );
                    });


                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(currentPage)
                            .currentSize(currentSize)
                            .totalPage(totalPage)
                            .totalElements(totalElements)
                            .numberOfElements(workspaceMemberInfoList.size())
                            .build();

                    // set page meta data last field to true or false
                    pageMeta.setLast(currentPage >= totalPage);
                    //pageMeta.setLast(workspaceMemberInfoList.size() == 0);

                    List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
                            .map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    return new DataProcess<>(new MemberInfoListResponse(memberInfoList, pageMeta));
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<MemberInfoResponse> loadMember(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Member, MemberInfoResponse>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<MemberInfoResponse> invokeDataProcess() {
                Member member = sessionService.getMember(workspaceId, sessionId, userId);
                MemberInfoResponse resultResponse;
                // mapping data
                resultResponse = modelMapper.map(member, MemberInfoResponse.class);
                return new DataProcess<>(resultResponse);
            }
        }.asApiResponse();
    }

    public ApiResponse<MemberSecessionResponse> deleteMember(String userId) {
        return new RepoDecoder<List<MemberHistory>, MemberSecessionResponse>(RepoDecoderType.DELETE) {
            @Override
            List<MemberHistory> loadFromDatabase() {
                return sessionService.getMemberHistoryList(userId);
            }

            @Override
            DataProcess<MemberSecessionResponse> invokeDataProcess() {
                List<MemberHistory> historyList = loadFromDatabase();
                for (MemberHistory memberHistory: historyList) {
                    memberHistory.setMemberType(MemberType.SECESSION);
                    sessionService.updateMemberHistory(memberHistory);
                }
                return new DataProcess<>(new MemberSecessionResponse(userId, true, LocalDateTime.now()));
            }
        }.asApiResponse();
    }

    //========================================= ROOM HISTORY INFORMATION RELATION ===========================================//
    public ApiResponse<RoomHistoryInfoListResponse> loadRoomHistoryInfoList(
            String workspaceId,
            String userId,
            boolean paging,
            Pageable pageable) {
        return new RepoDecoder<MemberHistory, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            @Override
            MemberHistory loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                if (!paging) {
                    // get all member history by uuid
                    PageRequest pageRequest = new PageRequest();
                    Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageRequest.of());

                    List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberPage.getContent().forEach(memberHistory -> {
                        roomHistoryList.add(memberHistory.getRoomHistory());
                    });


                    List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                    for(RoomHistory roomHistory: roomHistoryList) {
                        RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                        roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
                        roomHistoryInfoList.add(roomHistoryInfoResponse);
                    }


                    /*List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                            .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // find specific member has room history
                    /*List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberHistoryList.forEach(memberHistory -> {
                        if (memberHistory.getRoomHistory() != null) {
                            roomHistoryList.add(memberHistory.getRoomHistory());
                        }
                    });

                    List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                            .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(0)
                            .currentSize(0)
                            .numberOfElements(memberPage.getNumberOfElements())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getTotalElements())
                            .last(memberPage.isLast())
                            .build();

                    // Get Member List by Room Session Ids
                    for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                        List<MemberHistory> memberList = historyService.getMemberHistoryList(response.getSessionId());

                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = memberList.stream()
                                .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                                log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                                //todo://user infomation does not have role and role id change to workspace member info
                                WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                                memberInfoResponse.setRole(workspaceMemberData.getRole());
                                //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                                memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                                memberInfoResponse.setName(workspaceMemberData.getName());
                                memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                                memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                            }

                            Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                                @Override
                                public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                                    if (t1.getMemberType().equals(MemberType.LEADER)) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                    }
                    return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));

                } else {
                    //Page<RoomHistory> roomPage;
                    // get all member history by uuid
                    //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);
                    Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageable);
                    // get all member history by uuid
                    //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);
                    // find specific member has room history
                    /* List<RoomHistory> roomHistoryList = new ArrayList<>();
                        memberHistoryList.forEach(memberHistory -> {
                            if (memberHistory.getRoomHistory() != null) {
                                roomHistoryList.add(memberHistory.getRoomHistory());
                            }
                        });*/

                    //roomPage = this.roomHistoryRepository.findAll(pageable);
                    // find specific member has room history
                    List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberPage.getContent().forEach(memberHistory -> {
                        roomHistoryList.add(memberHistory.getRoomHistory());
                    });

                    List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                    for(RoomHistory roomHistory: roomHistoryList) {
                        RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                        roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
                        roomHistoryInfoList.add(roomHistoryInfoResponse);
                    }

                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(pageable.getPageNumber())
                            .currentSize(pageable.getPageSize())
                            .numberOfElements(memberPage.getNumberOfElements())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getTotalElements())
                            .last(memberPage.isLast())
                            .build();

                    //roomInfoList.forEach(info -> log.info("{}", info));
                    log.info("Paging Metadata: {}", pageMeta.toString());

                    // Get Member List by Room Session Ids
                    for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                        List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(response.getSessionId());

                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                                .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                                log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                                //todo://user infomation does not have role and role id change to workspace member info
                                WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                                memberInfoResponse.setRole(workspaceMemberData.getRole());
                                //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                                memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                                memberInfoResponse.setName(workspaceMemberData.getName());
                                memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                                memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                            }
                            Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                                @Override
                                public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                                    if (t1.getMemberType().equals(MemberType.LEADER)) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                        //log.debug("getRoomInfoList: {}", response.toString());
                    }
                    return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomHistoryDetailInfoResponse> loadRoomHistoryDetail(String workspaceId, String sessionId) {
        return new RepoDecoder<RoomHistory, RoomHistoryDetailInfoResponse>(RepoDecoderType.READ) {
            @Override
            RoomHistory loadFromDatabase() {
                return historyService.getRoomHistory(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomHistoryDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                //ApiResponse<RoomHistoryDetailInfoResponse> response = new ApiResponse<>();
                RoomHistory roomHistory = loadFromDatabase();
                if(roomHistory == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;*/
                } else {
                    // mapping data
                    RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);
                    resultResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    // Get Member List by Room Session ID
                    // Mapping Member List Data to Member Information List
                    List<MemberInfoResponse> memberInfoList = historyService.getMemberHistoryList(resultResponse.getSessionId())
                            .stream()
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // remove members who is evicted
                    memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                    // find and get extra information from use-server using uuid
                    if (!memberInfoList.isEmpty()) {
                        for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                            ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                            log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                            //todo://user infomation does not have role and role id change to workspace member info
                            WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                            memberInfoResponse.setRole(workspaceMemberData.getRole());
                            //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                            memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                            memberInfoResponse.setName(workspaceMemberData.getName());
                            memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                            memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                        }
                        Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                            @Override
                            public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                                if(t1.getMemberType().equals(MemberType.LEADER)) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                    }
                    // Set Member List to Room Detail Information Response
                    resultResponse.setMemberList(memberInfoList);
                    return new DataProcess<>(resultResponse);
                }

            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE ALL BY USER ID => [{}]", userId);
                ResultResponse resultResponse = new ResultResponse();
                List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(workspaceId, userId);
                historyService.removeRoomHistory(memberHistoryList);
                resultResponse.setResult(true);
                return new DataProcess<>(resultResponse);
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }
            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", roomHistoryDeleteRequest.getUuid());
                ResultResponse resultResponse = new ResultResponse();
                //resultResponse.setResult(false);
                //ApiResponse<ResultResponse> apiResponse = new ApiResponse<>(resultResponse);
                for (String sessionId: roomHistoryDeleteRequest.getSessionIdList()) {
                    log.info("ROOM HISTORY INFO DELETE BY SESSION ID => [{}]", sessionId);
                    MemberHistory memberHistory = historyService.getMemberHistory(workspaceId, sessionId, roomHistoryDeleteRequest.getUuid());
                    if(memberHistory == null) {
                        log.info("ROOM HISTORY INFO DELETE BUT MEMBER HISTORY DATA IS NULL BY SESSION ID => [{}]", sessionId);
                        //apiResponse.getData().setResult(false);
                        //apiResponse.setErrorResponseData(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND);
                    } else {
                        if (memberHistory.getUuid().equals(roomHistoryDeleteRequest.getUuid())) {
                            historyService.removeRoomHistory(memberHistory);
                        }
                    }
                }
                resultResponse.setResult(true);
                return new DataProcess<>(resultResponse);
            }
        }.asApiResponse();
    }
    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}
