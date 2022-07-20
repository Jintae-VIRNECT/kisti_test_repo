package com.virnect.serviceserver.servicedashboard.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.account.AccountRestService;
import com.virnect.data.application.record.RecordRestService;
import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.dao.file.RecordFileRepository;
import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.OrderType;
import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.domain.roomhistory.RoomHistorySortType;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.rest.RecordingFiles;
import com.virnect.data.dto.rest.UserInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RemoteServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.global.util.ListUtils;
import com.virnect.data.global.util.paging.CustomPaging;
import com.virnect.data.global.util.paging.PagingUtils;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFileInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFileUserInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.member.DashboardMemberInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.memberhistroy.DashboardMemberHistoryMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.record.DashboardRecordFileDetailMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.room.DashboardRoomDetailInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.room.OngoingRoomInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.roomhistory.DashboardRoomHistoryDetailInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.roomhistory.DashboardRoomHistoryInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.request.RoomHistoryListRequest;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileUserInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.HistoryCountResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.MemberInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomDetailInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomHistoryDetailInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomHistoryInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomHistoryInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class DashboardHistoryService {

	private final WorkspaceRestService workspaceRestService;
	private final AccountRestService accountRestService;
	private final RecordRestService recordRestService;

	private final RoomRepository roomRepository;
	private final RoomHistoryRepository roomHistoryRepository;
	private final MemberHistoryRepository memberHistoryRepository;

	private final FileRepository fileRepository;
	private final RecordFileRepository recordFileRepository;

	private final DashboardRoomDetailInfoMapper dashboardRoomDetailInfoMapper;
	private final DashboardMemberInfoMapper dashboardMemberInfoMapper;
	private final DashboardRoomHistoryDetailInfoMapper dashboardRoomHistoryDetailInfoMapper;
	private final DashboardRoomHistoryInfoMapper dashboardRoomHistoryInfoMapper;
	private final DashboardMemberHistoryMapper dashboardMemberHistoryMapper;
	private final DashboardFileInfoMapper dashboardFileInfoMapper;
	private final DashboardFileUserInfoMapper dashboardFileUserInfoMapper;
	private final DashboardRecordFileDetailMapper dashboardRecordFileDetailMapper;
	private final OngoingRoomInfoMapper ongoingRoomInfoMapper;

	public RoomHistoryInfoListResponse getRoomHistory(
		String workspaceId, String userId, RoomHistoryListRequest request
	) {
		List<RoomHistoryInfoResponse> roomHistories = new ArrayList<>();
		switch (request.getStatus()) {
			case ALL:
				ListUtils.addAllIfNotNull(roomHistories, getMyOnGoingRoomHistory(request, workspaceId, userId));
				ListUtils.addAllIfNotNull(roomHistories, getMyEndRoomHistory(request, workspaceId, userId));
				break;
			case ONGOING:
				ListUtils.addAllIfNotNull(roomHistories, getMyOnGoingRoomHistory(request, workspaceId, userId));
				break;
			case END:
				ListUtils.addAllIfNotNull(roomHistories, getMyEndRoomHistory(request, workspaceId, userId));
				break;
		}
		if (CollectionUtils.isEmpty(roomHistories)) {
			return RoomHistoryInfoListResponse.builder()
				.roomHistoryInfoList(Collections.emptyList())
				.pageMeta(PagingUtils.emptyPagingBuilder())
				.build();
		}
		return handleData(workspaceId, userId, request, roomHistories);
	}

	public HistoryCountResponse getRoomHistoryStatsInDate(
		String workspaceId,
		String userId,
		String selectedDate,
		int timeDifference
	) {

		int hourOfOneDay = 24;
		int[] myHistory = new int[hourOfOneDay];
		int[] entireHistory = new int[hourOfOneDay];
		long[] myDuration = new long[hourOfOneDay];
		long[] entireDuration = new long[hourOfOneDay];

		String historySessionTemp = "sessionId";
		int historyPeriodTemp = 0;
		int hourOfMemberHistory;
		int START_HOUR_INDEX = 0;
		int END_HOUR_INDEX = 23;

		LocalDateTime startDateTime = LocalDateTime.of(
			LocalDate.parse(selectedDate), LocalTime.of(0, 0, 0)).plusMinutes(timeDifference);
		LocalDateTime endDateTime = LocalDateTime.of(
			LocalDate.parse(selectedDate), LocalTime.of(23, 59, 59)).plusMinutes(timeDifference);

		// Load DB data
		List<MemberHistory> loadData = memberHistoryRepository.findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(
			workspaceId,
			startDateTime,
			endDateTime
		);

		for (MemberHistory memberHistory : loadData) {
			memberHistory.setStartDate(memberHistory.getStartDate().minusMinutes(timeDifference));
		}

		// Data handling
		int hour = START_HOUR_INDEX;
		while (hour <= END_HOUR_INDEX) {
			for (MemberHistory memberHistory : loadData) {
				hourOfMemberHistory = Integer.parseInt(
					memberHistory.getStartDate().format(DateTimeFormatter.ofPattern("HH")));

				if (hour == hourOfMemberHistory) {
					if (!(historyPeriodTemp == hourOfMemberHistory && historySessionTemp.equals(
						memberHistory.getSessionId()))) {
						historyPeriodTemp = hourOfMemberHistory;
						historySessionTemp = memberHistory.getSessionId();
						entireHistory[hour]++;
					}
					if (memberHistory.getUuid() != null) {
						if (memberHistory.getUuid().equals(userId)) {
							myHistory[hour]++;
							myDuration[hour] = myDuration[hour] + memberHistory.getDurationSec();
						}
					}
					entireDuration[hour] = entireDuration[hour] + memberHistory.getDurationSec();
				}
			}
			hour++;
		}
		return new HistoryCountResponse(myHistory, entireHistory, myDuration, entireDuration);
	}

	public HistoryCountResponse getRoomHistoryStatsOnMonth(
		String workspaceId,
		String userId,
		String selectedMonth,
		int timeDifference
	) {

		DateTimeFormatter customDateFormat = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM")
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
			.toFormatter();
		YearMonth yearMonth = YearMonth.from(LocalDate.parse(selectedMonth, customDateFormat));
		int lastDay = yearMonth.lengthOfMonth();
		int month = Integer.parseInt(yearMonth.format(DateTimeFormatter.ofPattern("MM")));

		int[] myHistory = new int[lastDay];
		int[] entireHistory = new int[lastDay];
		long[] myDuration = new long[lastDay];
		long[] entireDuration = new long[lastDay];

		String historySessionTemp = "sessionId";
		int historyPeriodTemp = 0;
		int dayOfMemberHistory;
		int monthOfMemberHistory;

		log.info("month : {}", month);

		LocalDateTime startDateTime = LocalDateTime.of(
			LocalDate.parse(selectedMonth + "-01"), LocalTime.of(0, 0, 0)).plusMinutes(timeDifference);
		LocalDateTime endDateTime = LocalDateTime.of(
			LocalDate.parse(selectedMonth + "-" + lastDay), LocalTime.of(23, 59, 59)).plusMinutes(timeDifference);

		// Load DB data
		List<MemberHistory> loadData = memberHistoryRepository.findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(
			workspaceId,
			startDateTime,
			endDateTime
		);
		for (MemberHistory memberHistory : loadData) {
			memberHistory.setStartDate(memberHistory.getStartDate().minusMinutes(timeDifference));
		}

		// Data handling
		int day = 0;
		while (day < lastDay) {
			for (MemberHistory memberHistory : loadData) {
				dayOfMemberHistory = Integer.parseInt(
					memberHistory.getStartDate().format(DateTimeFormatter.ofPattern("dd")));
				monthOfMemberHistory = Integer.parseInt(
					memberHistory.getStartDate().format(DateTimeFormatter.ofPattern("MM")));

				if (day + 1 == dayOfMemberHistory) {

					if (monthOfMemberHistory == month) {
						if (!(historyPeriodTemp == dayOfMemberHistory && historySessionTemp.equals(
							memberHistory.getSessionId()))) {
							historyPeriodTemp = dayOfMemberHistory;
							historySessionTemp = memberHistory.getSessionId();
							entireHistory[day]++;
						}
						entireDuration[day] = entireDuration[day] + memberHistory.getDurationSec();
					}
					if (memberHistory.getUuid().equals(userId)) {
						myHistory[day]++;
						myDuration[day] = myDuration[day] + memberHistory.getDurationSec();
					}
				}
			}
			day++;
		}

		return new HistoryCountResponse(myHistory, entireHistory, myDuration, entireDuration);
	}

	public RoomDetailInfoResponse getOngoingRoomDetail(String workspaceId, String sessionId) {

		Room ongoingRoom = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(()
				-> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		RoomDetailInfoResponse roomDetailInfoResponse = dashboardRoomDetailInfoMapper.toDto(ongoingRoom);
		roomDetailInfoResponse.setSessionType(ongoingRoom.getSessionProperty().getSessionType());

		List<MemberInfoResponse> memberInfoResponses = ongoingRoom.getMembers()
			.stream()
			.map(dashboardMemberInfoMapper::toDto)
			.collect(Collectors.toList());

		memberInfoResponses.removeIf(
			memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

		roomDetailInfoResponse.setMemberList(memberInfoResponses);

		// Set Member Info
		if (!roomDetailInfoResponse.getMemberList().isEmpty()) {
			for (MemberInfoResponse memberInfoResponse : roomDetailInfoResponse.getMemberList()) {
				ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMember(
					workspaceId, memberInfoResponse.getUuid());
				WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
				memberInfoResponse.setRole(workspaceMemberData.getRole());
				memberInfoResponse.setEmail(workspaceMemberData.getEmail());
				memberInfoResponse.setName(workspaceMemberData.getName());
				memberInfoResponse.setNickName(workspaceMemberData.getNickName());
				memberInfoResponse.setProfile(workspaceMemberData.getProfile());
			}
			roomDetailInfoResponse.setMemberList(setLeader(roomDetailInfoResponse.getMemberList()));
		}
		return roomDetailInfoResponse;
	}

	public RoomHistoryDetailInfoResponse getEndRoomDetail(
		String workspaceId,
		String sessionId
	) {

		RoomHistory endRoom = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_HISTORY_ROOM_NOT_FOUND));

		RoomHistoryDetailInfoResponse roomHistoryDetailInfoResponse = dashboardRoomHistoryDetailInfoMapper.toDto(
			endRoom);
		roomHistoryDetailInfoResponse.setSessionType(endRoom.getSessionPropertyHistory().getSessionType());

		List<MemberInfoResponse> memberInfoList = endRoom.getMemberHistories()
			.stream()
			.map(dashboardMemberHistoryMapper::toDto)
			.collect(Collectors.toList());

		memberInfoList.removeIf(
			memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

		roomHistoryDetailInfoResponse.setMemberList(memberInfoList);

		// Set Member Info
		if (!endRoom.getMemberHistories().isEmpty()) {
			for (MemberInfoResponse memberInfoResponse : roomHistoryDetailInfoResponse.getMemberList()) {
				ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo =
					workspaceRestService.getWorkspaceMember(workspaceId, memberInfoResponse.getUuid());
				WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
				memberInfoResponse.setRole(workspaceMemberData.getRole());
				memberInfoResponse.setEmail(workspaceMemberData.getEmail());
				memberInfoResponse.setName(workspaceMemberData.getName());
				memberInfoResponse.setNickName(workspaceMemberData.getNickName());
				memberInfoResponse.setProfile(workspaceMemberData.getProfile());
			}
			roomHistoryDetailInfoResponse.setMemberList(setLeader(roomHistoryDetailInfoResponse.getMemberList()));
		}
		return roomHistoryDetailInfoResponse;
	}

	public RoomHistoryInfoListResponse handleData(
		String workspaceId,
		String userId,
		RoomHistoryListRequest request,
		List<RoomHistoryInfoResponse> roomHistories
	) {
		List<WorkspaceMemberInfoResponse> workspaceMembers = workspaceRestService.getWorkspaceMembersCustom(workspaceId)
			.getData()
			.getMemberInfoList();
		List<FileDetailInfoResponse> localRecFileAll = getLocalRecordFileList(
			workspaceId,
			//roomHistory.getSessionId(),
			false
		);
		List<FileInfoResponse> attachFileAll = getAttachedFileList(
			workspaceId,
			//roomHistory.getSessionId(),
			false
		);
		List<RecordingFiles> serverRecFileAll = recordRestService.getServerRecordFileList(workspaceId, "DASHBOARD")
			.getData()
			.getInfos();
		if (CollectionUtils.isEmpty(serverRecFileAll)) {
			serverRecFileAll = new ArrayList<>();
		}
		log.info("RECORD SERVER LIST SIZE : " + serverRecFileAll.size());

		for (RoomHistoryInfoResponse roomHistory : roomHistories) {
			for (MemberInfoResponse memberInfoResponse : roomHistory.getMemberList()) {
				for (WorkspaceMemberInfoResponse memberInfo : workspaceMembers) {
					if (memberInfo.getUuid() != null && memberInfoResponse.getUuid() != null) {
						if (memberInfo.getUuid().equals(memberInfoResponse.getUuid())) {
							memberInfoResponse.setRole(memberInfo.getRole());
							memberInfoResponse.setEmail(memberInfo.getEmail());
							memberInfoResponse.setName(memberInfo.getName());
							memberInfoResponse.setNickName(memberInfo.getNickName());
							memberInfoResponse.setProfile(memberInfo.getProfile());
						}
					}
				}
			}

			roomHistory.setMemberList(roomHistory.getMemberList());
			roomHistory.setMemberList(setLeader(roomHistory.getMemberList()));

			roomHistory.setLeaderNickName(
				roomHistory.getMemberList()
					.stream()
					.filter(memberInfoResponse -> memberInfoResponse.getMemberType() == MemberType.LEADER)
					.map(MemberInfoResponse::getNickName)
					.collect(Collectors.joining())
			);

			roomHistory.setServerRecord(
				serverRecFileAll
					.stream()
					.filter(recordingFiles -> recordingFiles.getSessionId().equals(roomHistory.getSessionId()))
					.count()
			);
			roomHistory.setLocalRecord(
				localRecFileAll
					.stream()
					.filter(recordFiles -> recordFiles.getSessionId().equals(roomHistory.getSessionId()))
					.count()
			);
			roomHistory.setAttach(
				attachFileAll
					.stream()
					.filter(fileInfoResponse -> fileInfoResponse.getSessionId().equals(roomHistory.getSessionId()))
					.count()
			);
		}

		if (request.getSearchWord() != null) {
			roomHistories = roomHistories.stream()
				.filter(roomInfo -> roomInfo.getTitle().contains(request.getSearchWord())
					|| roomInfo.getMemberList().stream()
					.anyMatch(memberInfo -> memberInfo.getNickName().contains(request.getSearchWord()))
				)
				.collect(Collectors.toList());
		}

		// 리스트 정렬 및 글 넘버 Setting
		roomHistories = sortingAndSetNumber(
			roomHistories,
			request.getSortProperties(),
			request.getSortOrder()
		);

		CustomPaging customPaging = PagingUtils.customPaging(
			request.getPage(), roomHistories.size(), request.getSize(), roomHistories.isEmpty());

		// 데이터 range
		roomHistories = IntStream
			.range(customPaging.getStartIndex(), customPaging.getEndIndex())
			.mapToObj(roomHistories::get)
			.collect(Collectors.toList());

		// 페이징 데이터 설정
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(customPaging.getCurrentPage())
			.currentSize(customPaging.getSize())
			.numberOfElements(roomHistories.size())
			.totalPage(customPaging.getTotalPage())
			.totalElements(customPaging.getTotalElements())
			.last(customPaging.isLast())
			.build();

		return RoomHistoryInfoListResponse.builder()
			.roomHistoryInfoList(roomHistories)
			.pageMeta(pageMeta)
			.build();
	}

	public List<FileInfoResponse> getAttachedFileList(
		String workspaceId,
		boolean deleted
	) {
		List<FileInfoResponse> fileInfoResponses = new ArrayList<>();
		try {
			List<File> files = fileRepository.findByWorkspaceIdAndDeleted(workspaceId, deleted);
			if (files.size() > 0) {
				fileInfoResponses = files.stream()
					.map(dashboardFileInfoMapper::toDto)
					.collect(Collectors.toList());
			}
		} catch (Exception exception) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return fileInfoResponses;
	}

	public List<FileDetailInfoResponse> getLocalRecordFileList(
		String workspaceId,
		boolean deleted
	) {
		List<FileDetailInfoResponse> fileDetailInfoResponses = new ArrayList<>();
		try {
			List<RecordFile> recordFiles = recordFileRepository.findByWorkspaceIdAndDeleted(workspaceId, deleted);

			ApiResponse<UserInfoListResponse> listResponse = accountRestService.getUserInfo(false);
			UserInfoResponse userInfo = null;
			for (RecordFile recordFile : recordFiles) {
				for (UserInfoResponse response : listResponse.getData().getUserInfoList()) {
					if (response.getUuid().equals(recordFile.getUuid())) {
						userInfo = response;
					}
				}
				FileUserInfoResponse fileUserInfoResponse = dashboardFileUserInfoMapper.toDto(userInfo);
				FileDetailInfoResponse fileDetailInfoResponse = dashboardRecordFileDetailMapper.toDto(recordFile);
				fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
				fileDetailInfoResponses.add(fileDetailInfoResponse);
			}
		} catch (Exception exception) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return fileDetailInfoResponses;
	}

	public List<RoomHistoryInfoResponse> sortingAndSetNumber(
		List<RoomHistoryInfoResponse> roomHistoryInfoList,
		RoomHistorySortType sortProperties,
		OrderType sortOrder
	) {
		if (sortProperties == RoomHistorySortType.SERVER_RECORD_FILE_COUNT) {
			roomHistoryInfoList.sort((room1, room2) -> {
				int result = 0;
				Long count1 = room1.getServerRecord();
				Long count2 = room2.getServerRecord();
				return getCountOrderResult(sortOrder, result, count1, count2);
			});
		} else if (sortProperties == RoomHistorySortType.LOCAL_RECORD_FILE_COUNT) {
			roomHistoryInfoList.sort((room1, room2) -> {
				int result = 0;
				Long count1 = room1.getLocalRecord();
				Long count2 = room2.getLocalRecord();
				return getCountOrderResult(sortOrder, result, count1, count2);
			});
		} else if (sortProperties == RoomHistorySortType.ATTACHED_FILE_COUNT) {
			roomHistoryInfoList.sort((room1, room2) -> {
				int result = 0;
				Long count1 = room1.getAttach();
				Long count2 = room2.getAttach();
				return getCountOrderResult(sortOrder, result, count1, count2);
			});
		} else if (sortProperties == RoomHistorySortType.ACTIVE_DATE || sortProperties == RoomHistorySortType.NO) {
			roomHistoryInfoList.sort((room1, room2) -> {
				int result = 0;
				LocalDateTime date1 = room1.getActiveDate();
				LocalDateTime date2 = room2.getActiveDate();
				if (sortOrder == OrderType.ASC) {
					if (date1.isAfter(date2)) {
						result = 1;
					} else {
						result = -1;
					}
				} else if (sortOrder == OrderType.DESC) {
					if (date1.isBefore(date2)) {
						result = 1;
					} else {
						result = -1;
					}
				}
				return result;
			});
		} else if (sortProperties == RoomHistorySortType.TITLE) {
			roomHistoryInfoList.sort((room1, room2) -> {
				int result = 0;
				String title1 = room1.getTitle();
				String title2 = room2.getTitle();
				if (sortOrder == OrderType.ASC) {
					if (title1.equals(title2)) {
						return result;
					} else {
						result = title1.compareTo(title2);
						if (result > 0) {
							result = 1;
						} else {
							result = -1;
						}
					}
				} else if (sortOrder == OrderType.DESC) {
					if (title1.equals(title2)) {
						return result;
					} else {
						result = title1.compareTo(title2);
						if (result < 0) {
							result = 1;
						} else {
							result = -1;
						}
					}
				}
				return result;
			});
		} else if (sortProperties == RoomHistorySortType.LEADER_NICK_NAME) {
			roomHistoryInfoList.sort(new Comparator<RoomHistoryInfoResponse>() {
				@Override
				public int compare(RoomHistoryInfoResponse room1, RoomHistoryInfoResponse room2) {
					return extractInt(room1.getLeaderNickName(), room2.getLeaderNickName());
				}

				int extractInt(String name1, String name2) {
					int result;
					String removeString1;
					String removeString2;
					int remoteStringToInt1;
					int remoteStringToInt2;

					name1 = name1 == null || name1.isEmpty() ? "" : name1;
					name2 = name2 == null || name2.isEmpty() ? "" : name2;

					if (name1.equals(name2)) {
						result = 0;
					} else {
						String removeInteger1 = name1.replaceAll("[0-9]", "");
						String removeInteger2 = name2.replaceAll("[0-9]", "");
						if (removeInteger1.equals(removeInteger2)) {
							removeString1 = name1.replaceAll("\\D", "");
							removeString2 = name2.replaceAll("\\D", "");
							remoteStringToInt1 = removeString1.isEmpty() ? 0 : Integer.parseInt(removeString1);
							remoteStringToInt2 = removeString2.isEmpty() ? 0 : Integer.parseInt(removeString2);

							result = remoteStringToInt1 - remoteStringToInt2;
							if (sortOrder == OrderType.ASC) {
								if (result > 0) {
									result = 1;
								} else {
									result = -1;
								}
							} else if (sortOrder == OrderType.DESC) {
								if (result < 0) {
									result = 1;
								} else {
									result = -1;
								}
							}
						} else {
							result = name1.compareTo(name2);
							if (sortOrder == OrderType.ASC) {
								if (result > 0) {
									result = 1;
								} else {
									result = -1;
								}
							} else if (sortOrder == OrderType.DESC) {
								if (result < 0) {
									result = 1;
								} else {
									result = -1;
								}
							}
						}
					}
					return result;
				}
			});
		} else if (sortProperties == RoomHistorySortType.STATUS) {
			roomHistoryInfoList.sort((room1, room2) -> {
				boolean status1 = room1.isStatus();
				boolean status2 = room2.isStatus();
				int result = 0;

				if (sortOrder == OrderType.ASC) {
					if (status1 != status2) {
						int booleanResult = Boolean.compare(status1, status2);

						if (booleanResult == 1) {
							result = 1;
						} else {
							result = -1;
						}
					}
				} else if (sortOrder == OrderType.DESC) {
					if (status1 != status2) {
						int booleanResult = Boolean.compare(status1, status2);

						if (booleanResult != 1) {
							result = 1;
						} else {
							result = -1;
						}
					}
				}
				return result;
			});
		}

		if (sortProperties == RoomHistorySortType.NO && sortOrder == OrderType.ASC) {
			int startNo = 1;
			for (RoomHistoryInfoResponse roomHistoryInfoResponse : roomHistoryInfoList) {
				roomHistoryInfoResponse.setNo(startNo);
				startNo++;
			}
		} else {
			int startNo = roomHistoryInfoList.size();
			for (RoomHistoryInfoResponse roomHistoryInfoResponse : roomHistoryInfoList) {
				roomHistoryInfoResponse.setNo(startNo);
				startNo--;
			}
		}

		return roomHistoryInfoList;
	}

	private int getCountOrderResult(
		OrderType sortOrder,
		int result,
		Long count1,
		Long count2
	) {
		if (sortOrder == OrderType.ASC) {
			if (count1.equals(count2))
				result = 0;
			else if (count1 > count2)
				result = 1;
			else
				result = -1;
		} else if (sortOrder == OrderType.DESC) {
			if (count1.equals(count2))
				result = 0;
			else if (count1 < count2)
				result = 1;
			else
				result = -1;
		}
		return result;
	}

	public List<RoomHistoryInfoResponse> getMyOnGoingRoomHistory(
		RoomHistoryListRequest option, String workspaceId, String userId
	) {

		List<Room> myRoomHistory = roomRepository.findRoomHistoryInWorkspaceWithDateOrSpecificUserId(
			option.getSearchStartDate(),
			option.getSearchEndDate(),
			workspaceId,
			userId
		);

		return myRoomHistory.stream().map(room -> {
			RoomHistoryInfoResponse roomHistoryInfoResponse = ongoingRoomInfoMapper.toDto(room);
			roomHistoryInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

			List<MemberInfoResponse> memberInfoResponses = room.getMembers().stream()
				.filter(member -> member.getMemberStatus() != MemberStatus.EVICTED)
				.map(dashboardMemberInfoMapper::toDto)
				.collect(Collectors.toList());

			roomHistoryInfoResponse.setStatus(false);
			roomHistoryInfoResponse.setMemberList(memberInfoResponses);
			return roomHistoryInfoResponse;
		}).collect(Collectors.toList());
	}

	public List<RoomHistoryInfoResponse> getMyEndRoomHistory(
		RoomHistoryListRequest option, String workspaceId, String userId
	) {

		List<RoomHistory> roomHistories = roomHistoryRepository.findRoomHistoryInWorkspaceIdWithDateOrSpecificUserId(
			option.getSearchStartDate(), option.getSearchEndDate(), workspaceId, userId
		);

		return roomHistories.stream().map(roomHistory -> {
			RoomHistoryInfoResponse endRoomHistoryInfoResponse = dashboardRoomHistoryInfoMapper.toDto(roomHistory);
			endRoomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
			endRoomHistoryInfoResponse.setStatus(true);

			// MemberHistory 도메인 객체의 경우 MemberStatus 값이 없기에, 회원 정보 필터링 불가능
			List<MemberInfoResponse> memberInfoResponses = roomHistory.getMemberHistories().stream()
				.map(dashboardMemberHistoryMapper::toDto)
				.collect(Collectors.toList());

			endRoomHistoryInfoResponse.setMemberList(memberInfoResponses);
			return endRoomHistoryInfoResponse;
		}).collect(Collectors.toList());
	}

	private List<MemberInfoResponse> setLeader(List<MemberInfoResponse> members) {
		members.sort((t1, t2) -> {
			if (t1.getMemberType().equals(MemberType.LEADER)) {
				return 1;
			}
			return 0;
		});
		return members;
	}

}
