package com.virnect.serviceserver.dao;

import com.virnect.data.dao.*;
import com.virnect.service.ApiResponse;
import com.virnect.serviceserver.dto.constraint.LicenseConstants;
import com.virnect.serviceserver.dto.response.PageMetadataResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.serviceserver.dto.response.member.MemberInfoListResponse;
import com.virnect.serviceserver.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.dto.response.member.MemberSecessionResponse;
import com.virnect.serviceserver.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberDataRepository extends DataRepository {
    private static final String TAG = MemberDataRepository.class.getSimpleName();

    //========================================= MEMBER INFORMATION RELATION =================================================//
    public ApiResponse<WorkspaceMemberInfoListResponse> loadMemberList(String workspaceId, String filter, String search, int page, int size) {
        return new RepoDecoder<ApiResponse<WorkspaceMemberInfoListResponse>, WorkspaceMemberInfoListResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<WorkspaceMemberInfoListResponse> loadFromDatabase() {
                log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
                return workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, search, page, size);
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

    public ApiResponse<MemberInfoListResponse> loadMemberList(String workspaceId, String userId, String filter, String search, int page, int size) {
        return new RepoDecoder<ApiResponse<WorkspaceMemberInfoListResponse>, MemberInfoListResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<WorkspaceMemberInfoListResponse> loadFromDatabase() {
                log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
                return workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, search, page, size);
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

    public ApiResponse<MemberInfoListResponse> loadMemberList(String workspaceId, String sessionId, String userId, String filter, String search, int page, int size) {
        return new RepoDecoder<ApiResponse<WorkspaceMemberInfoListResponse>, MemberInfoListResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<WorkspaceMemberInfoListResponse> loadFromDatabase() {
                log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
                return workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, search, page, size);
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
}
