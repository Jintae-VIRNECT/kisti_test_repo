package com.virnect.serviceserver.application;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.remote.application.workspace.WorkspaceRestService;
import com.virnect.data.dto.constraint.LicenseConstants;
import com.virnect.data.dto.response.PageMetadataResponse;
import com.virnect.data.dto.response.member.MemberInfoListResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.dto.response.member.MemberSecessionResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	/*private final ModelMapper modelMapper;

	private final SessionService sessionService;

	private final WorkspaceRestService workspaceRestService;


	public WorkspaceMemberInfoListResponse getMembers(
		String workspaceId,
		String filter,
		String search,
		int page,
		int size
	) {

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, page, size).getData();

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();
		int currentPage = responseData.getPageMeta().getCurrentPage();
		int totalPage = responseData.getPageMeta().getTotalPage();

		// set Page Metadata
		responseData.getPageMeta().setNumberOfElements(workspaceMemberInfoList.size());
		responseData.getPageMeta().setLast(currentPage >= totalPage);

		return responseData;
	}

	public MemberInfoListResponse getMembersExceptForMe(
		String workspaceId,
		String userId,
		String filter,
		String search,
		int page,
		int size
	) {
		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, page, size).getData();

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();
		PageMetadataResponse workspaceMemberPageMeta = responseData.getPageMeta();
		int currentPage = workspaceMemberPageMeta.getCurrentPage();
		int currentSize = workspaceMemberPageMeta.getCurrentSize();
		int totalPage = workspaceMemberPageMeta.getTotalPage();
		long totalElements = workspaceMemberPageMeta.getTotalElements();

		//remove members who does not have any license plan or remote license
		workspaceMemberInfoList.removeIf(memberInfoResponses ->
			Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
				!Arrays.toString(memberInfoResponses.getLicenseProducts())
					.contains(LicenseConstants.PRODUCT_NAME));

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

		return new MemberInfoListResponse(memberInfoList, pageMeta);
	}

	public MemberInfoListResponse getMembersInvitePossible(
		String workspaceId,
		String sessionId,
		String userId,
		String filter,
		String search,
		int page,
		int size
	) {
		MemberInfoListResponse responseData = null;

		Room room = sessionService.getRoom(workspaceId, sessionId);
		if (room == null) {
			// insert return custom error
		} else {
			// Get Member List from Room
			// Mapping Member List Data to Member Information List
			List<Member> memberList = room.getMembers();
			//remove members who does not have room id
			memberList.removeIf(member -> member.getRoom() == null);

			//fetch workspace member information from workspace
			WorkspaceMemberInfoListResponse feignResponse = workspaceRestService.getWorkspaceMemberInfoList(
				workspaceId, filter, search, page, size).getData();

			List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse
				.getMemberInfoList();
			PageMetadataResponse workspaceMemberPageMeta = feignResponse.getPageMeta();
			int currentPage = workspaceMemberPageMeta.getCurrentPage();
			int currentSize = workspaceMemberPageMeta.getCurrentSize();
			int totalPage = workspaceMemberPageMeta.getTotalPage();
			long totalElements = workspaceMemberPageMeta.getTotalElements();

			//remove members who does not have any license plan or remote license
			workspaceMemberInfoList.removeIf(memberInfoResponses ->
				Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
					!Arrays.toString(memberInfoResponses.getLicenseProducts())
						.contains(LicenseConstants.PRODUCT_NAME));

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

			responseData = new MemberInfoListResponse(memberInfoList, pageMeta);
		}
		return responseData;
	}

	public MemberSecessionResponse deleteMembersBySession(String userId) {
		List<MemberHistory> historyList = sessionService.getMemberHistoryList(userId);
		for (MemberHistory memberHistory : historyList) {
			memberHistory.setMemberType(MemberType.SECESSION);
			sessionService.updateMemberHistory(memberHistory);
		}
		return new MemberSecessionResponse(userId, true, LocalDateTime.now());
	}*/
}
