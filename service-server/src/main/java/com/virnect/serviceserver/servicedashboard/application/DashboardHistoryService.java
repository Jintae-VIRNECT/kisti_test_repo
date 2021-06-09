package com.virnect.serviceserver.servicedashboard.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.record.RecordRestService;
import com.virnect.data.application.user.UserRestService;
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
import com.virnect.data.dto.rest.RecordServerFileInfoResponse;
import com.virnect.data.dto.rest.UserInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.global.util.ListUtils;
import com.virnect.serviceserver.servicedashboard.dto.PageMetadataResponse;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFileInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFileUserInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.member.DashboardMemberInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.memberhistroy.DashboardMemberHistoryMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.record.DashboardRecordFileDetailMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.room.OngoingRoomInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.room.DashboardRoomDetailInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.roomhistory.DashboardRoomHistoryDetailInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.roomhistory.DashboardRoomHistoryInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.request.RoomHistoryDetailRequest;
import com.virnect.serviceserver.servicedashboard.dto.request.RoomHistoryListRequest;
import com.virnect.serviceserver.servicedashboard.dto.request.RoomHistoryStatsRequest;
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

	//private final ModelMapper modelMapper;

	private final WorkspaceRestService workspaceRestService;
	private final UserRestService userRestService;
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

	/**
	 * 협업 히스토리 요청 처리
	 *
	 * @param request - 파일 요청 데이터
	 * @return - 로컬 녹화 파일  URL 정보
	 */
	public RoomHistoryInfoListResponse getRoomHistory(
		RoomHistoryListRequest request, String workspaceId, String userId
	) {

		RoomHistoryInfoListResponse roomHistoryInfoListResponse;

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

		roomHistoryInfoListResponse = handleData(request, roomHistories);

		return roomHistoryInfoListResponse;
	}

	/**
	 * 선택 일(Date)의 협업 히스토리 통계 요청 처리
	 *
	 * @param request - 파일 요청 데이터
	 * @return - 본인 및 전체 히스토리 횟수 및 시간 통계
	 */
	public HistoryCountResponse getRoomHistoryStatsInDate(RoomHistoryStatsRequest request) {

		List<MemberHistory> loadData;

		LocalDateTime startDateTime;
		LocalDateTime endDateTime;

		int historyPeriodTemp = 0;
		String historySessionTemp = "sessionId";

		int hourOfMemberHistory;
		int hourOfOneDay = 24;
		int START_HOUR_INDEX = 0;
		int END_HOUR_INDEX = 23;
		int[] myHistory = new int[hourOfOneDay];
		int[] entireHistory = new int[hourOfOneDay];
		long[] myDuration = new long[hourOfOneDay];
		long[] entireDuration = new long[hourOfOneDay];

		try {
			startDateTime = LocalDateTime.of(
				LocalDate.parse(request.getPeriod()),
				LocalTime.of(0, 0, 0)
			).plusMinutes(request.getDiffTime());
			endDateTime = LocalDateTime.of(
				LocalDate.parse(request.getPeriod()),
				LocalTime.of(23, 59, 59)
			).plusMinutes(request.getDiffTime());

			// Load DB data
			loadData = memberHistoryRepository.findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(
				request.getWorkspaceId(), startDateTime, endDateTime);

			for (MemberHistory memberHistory : loadData) {
				memberHistory.setStartDate(memberHistory.getStartDate().minusMinutes(request.getDiffTime()));
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
							if (memberHistory.getUuid().equals(request.getUserId())) {
								myHistory[hour]++;
								myDuration[hour] = myDuration[hour] + memberHistory.getDurationSec();
							}
						}
						entireDuration[hour] = entireDuration[hour] + memberHistory.getDurationSec();
					}
				}
				hour++;
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_HISTORY_STATS_IN_DATE);
		}
		return new HistoryCountResponse(myHistory, entireHistory, myDuration, entireDuration);
	}

	/**
	 * 선택 월(Month)의 협업 히스토리 통계 요청 처리
	 *
	 * @param request - 파일 요청 데이터
	 * @return - 본인 및 전체 히스토리 횟수 및 시간 통계
	 */
	public HistoryCountResponse getRoomHistoryStatsOnMonth(RoomHistoryStatsRequest request) {

		DateTimeFormatter customDateFormat = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM")
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
			.toFormatter();
		YearMonth yearMonth = YearMonth.from(LocalDate.parse(request.getPeriod(), customDateFormat));

		List<MemberHistory> loadData;
		LocalDateTime startDateTime;
		LocalDateTime endDateTime;

		int[] myHistory;
		int[] entireHistory;
		long[] myDuration;
		long[] entireDuration;

		int historyPeriodTemp = 0;
		String historySessionTemp = "sessionId";

		int dayOfMemberHistory;
		int monthOfMemberHistory;
		int lastDay = yearMonth.lengthOfMonth();
		int selectedMonth = Integer.parseInt(yearMonth.format(DateTimeFormatter.ofPattern("MM")));

		log.info("month : {}", selectedMonth);

		startDateTime = LocalDateTime.of(
			LocalDate.parse(request.getPeriod() + "-01"),
			LocalTime.of(0, 0, 0)
		).plusMinutes(request.getDiffTime());
		endDateTime = LocalDateTime.of(
			LocalDate.parse(request.getPeriod() + "-" + lastDay),
			LocalTime.of(23, 59, 59)
		).plusMinutes(request.getDiffTime());

		myHistory = new int[lastDay];
		entireHistory = new int[lastDay];
		myDuration = new long[lastDay];
		entireDuration = new long[lastDay];

		try {
			// Load DB data
			loadData = memberHistoryRepository.findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(
				request.getWorkspaceId(),
				startDateTime,
				endDateTime
			);

			for (MemberHistory memberHistory : loadData) {
				memberHistory.setStartDate(memberHistory.getStartDate().minusMinutes(request.getDiffTime()));
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

						if (monthOfMemberHistory == selectedMonth) {
							if (!(historyPeriodTemp == dayOfMemberHistory && historySessionTemp.equals(
								memberHistory.getSessionId()))) {
								historyPeriodTemp = dayOfMemberHistory;
								historySessionTemp = memberHistory.getSessionId();
								entireHistory[day]++;
							}
							entireDuration[day] = entireDuration[day] + memberHistory.getDurationSec();
						}

						if (memberHistory.getUuid().equals(request.getUserId())) {
							myHistory[day]++;
							myDuration[day] = myDuration[day] + memberHistory.getDurationSec();
						}
					}
				}
				day++;
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_HISTORY_STATS_ON_MONTH);
		}
		return new HistoryCountResponse(myHistory, entireHistory, myDuration, entireDuration);
	}

	/**
	 * 진행중인 협업 상세정보 요청 처리
	 * @param workspaceId - 협업이 진행중인 워크스페이스 고유 식별자
	 * @param sessionId - 진행중인 협업의 세션 식별자
	 * @return - 협업 상세 정보
	 */
	public RoomDetailInfoResponse getOngoingRoomDetail(String workspaceId, String sessionId) {

		RoomDetailInfoResponse roomDetailInfoResponse;

		Room ongoingRoom = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElseThrow(()
			-> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		try {
			roomDetailInfoResponse = dashboardRoomDetailInfoMapper.toDto(ongoingRoom);
			//roomDetailInfoResponse = modelMapper.map(ongoingRoom, RoomDetailInfoResponse.class);
			roomDetailInfoResponse.setSessionType(ongoingRoom.getSessionProperty().getSessionType());

			List<MemberInfoResponse> memberInfoResponses = ongoingRoom.getMembers()
				.stream()
				//.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.map(member -> dashboardMemberInfoMapper.toDto(member))
				.collect(Collectors.toList());

			memberInfoResponses.removeIf(
				memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

			roomDetailInfoResponse.setMemberList(memberInfoResponses);

			// Set Member Info
			if (!roomDetailInfoResponse.getMemberList().isEmpty()) {

				for (MemberInfoResponse memberInfoResponse : roomDetailInfoResponse.getMemberList()) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo =
						workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}

				roomDetailInfoResponse.setMemberList(
					setLeader(roomDetailInfoResponse.getMemberList())
				);

			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_MAPPER);
		}
		return roomDetailInfoResponse;
	}

	/**
	 * 종료된 협업 상세정보 요청 처리
	 *
	 * @param request - 협업 요청 데이터
	 * @return - 종료된 협업 상세 정보
	 */
	public RoomHistoryDetailInfoResponse getEndRoomDetail(RoomHistoryDetailRequest request) {

		RoomHistoryDetailInfoResponse roomHistoryDetailInfoResponse;

		RoomHistory endRoom = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(
			request.getWorkspaceId(),
			request.getSessionId()
		).orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_HISTORY_FOUND));

		try {
			//roomHistoryDetailInfoResponse = modelMapper.map(endRoom, RoomHistoryDetailInfoResponse.class);
			roomHistoryDetailInfoResponse = dashboardRoomHistoryDetailInfoMapper.toDto(endRoom);
			roomHistoryDetailInfoResponse.setSessionType(endRoom.getSessionPropertyHistory().getSessionType());

			List<MemberInfoResponse> memberInfoList = endRoom.getMemberHistories()
				.stream()
				//.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.map(memberHistory -> dashboardMemberHistoryMapper.toDto(memberHistory))
				.collect(Collectors.toList());

			memberInfoList.removeIf(
				memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

			roomHistoryDetailInfoResponse.setMemberList(memberInfoList);

			// Set Member Info
			if (!endRoom.getMemberHistories().isEmpty()) {
				for (MemberInfoResponse memberInfoResponse : roomHistoryDetailInfoResponse.getMemberList()) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo =
						workspaceRestService.getWorkspaceMemberInfo(request.getWorkspaceId(), memberInfoResponse.getUuid());
					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}

				roomHistoryDetailInfoResponse.setMemberList(
					setLeader(roomHistoryDetailInfoResponse.getMemberList())
				);

			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_HISTORY_MAPPER);

		}
		return roomHistoryDetailInfoResponse;
	}

	/*public List<RoomHistoryInfoResponse> makeOngoingRooms(RoomHistoryListRequest request) {

		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;
		boolean useFromTo = request.getFromTo() != null && !request.getFromTo().isEmpty();
		boolean useUserId = request.getUserId() != null && !request.getUserId().isEmpty();
		String[] fromToSplit = useFromTo ? request.getFromTo().split(",") : null;
		if (useFromTo) {
			startDateTime = LocalDateTime.of(LocalDate.parse(fromToSplit[0].toString()), LocalTime.of(0, 0, 0));
			endDateTime = LocalDateTime.of(LocalDate.parse(fromToSplit[1].toString()), LocalTime.of(23, 59, 59));
		}

		List<Room> ongoingRooms;
		if (useFromTo) {
			ongoingRooms = roomRepository.findByWorkspaceIdAndRoomStatusAndActiveDateBetween(
				request.getWorkspaceId(), RoomStatus.ACTIVE, startDateTime, endDateTime);
		} else {
			ongoingRooms = roomRepository.findByWorkspaceIdAndRoomStatus(
				request.getWorkspaceId(), RoomStatus.ACTIVE);
		}
		List<Member> ongoingRoomMemberHistories = memberRepository.findByWorkspaceIdAndRoomNotNull(
			request.getWorkspaceId());
		return ongoingRoomMapper(ongoingRooms, ongoingRoomMemberHistories);
	}*/

	/*public List<RoomHistoryInfoResponse> makeEndRooms(RoomHistoryListRequest request) {

		List<RoomHistory> endRooms = new ArrayList<>();

		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;
		boolean useFromTo = request.getFromTo() != null && !request.getFromTo().isEmpty();
		boolean useUserId = request.getUserId() != null && !request.getUserId().isEmpty();
		String[] fromToSplit = useFromTo ? request.getFromTo().split(",") : null;

		if (useFromTo) {
			startDateTime = LocalDateTime.of(LocalDate.parse(fromToSplit[0].toString()), LocalTime.of(0, 0, 0));
			endDateTime = LocalDateTime.of(LocalDate.parse(fromToSplit[1].toString()), LocalTime.of(23, 59, 59));
		}

		if (useUserId) {
			if (useFromTo) {
				endRooms = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndMemberHistories_UuidAndUnactiveDateBetween(
					request.getWorkspaceId(),
					request.getUserId(),
					startDateTime,
					endDateTime
				);
			} else {
				endRooms = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndMemberHistories_Uuid(
					request.getWorkspaceId(),
					request.getUserId()
				);
			}
		} else {
			if (useFromTo) {
				endRooms = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndUnactiveDateBetween(
					request.getWorkspaceId(),
					startDateTime,
					endDateTime
				);
			} else {
				endRooms = roomHistoryRepository.findRoomHistoryByWorkspaceId(request.getWorkspaceId());
			}
		}

		List<MemberHistory> endRoomMemberHistories = memberHistoryRepository.findByWorkspaceId(
			request.getWorkspaceId());
		return endRoomMapper(endRooms, endRoomMemberHistories);
	}*/

	/**
	 * 협업 히스토리 데이터 핸들링 처리
	 * @param request - Request 옵션(workspaceId, sessionId ..) 
	 * @param roomHistories - 대상 데이터
	 * @return - 핸들링 된 협업 히스토리
	 */
	public RoomHistoryInfoListResponse handleData(
		RoomHistoryListRequest request, List<RoomHistoryInfoResponse> roomHistories
	) {

		RoomHistoryInfoListResponse roomHistoryInfoListResponse;

		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(0)
			.currentSize(0)
			.numberOfElements(roomHistories.size())
			.totalPage(0)
			.totalElements(roomHistories.size())
			.last(true)
			.build();

		if (roomHistories.size() > 0) {
			try {

				List<WorkspaceMemberInfoResponse> workspaceMembers =
					workspaceRestService.getWorkspaceMembers(request.getWorkspaceId()).getData().getMemberInfoList();

				List<FileDetailInfoResponse> localRecFileAll = getLocalRecordFileList(
					request.getWorkspaceId(),
					//roomHistory.getSessionId(),
					false
				);
				List<FileInfoResponse> attachFileAll = getAttachedFileList(
					request.getWorkspaceId(),
					//roomHistory.getSessionId(),
					false
				);
				List<RecordServerFileInfoResponse> serverRecFileAll =
					recordRestService.getServerRecordFileList(
						request.getWorkspaceId(), "DASHBOARD")
						.getData()
						.getInfos();


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

					roomHistory.setMemberList(
						setLeader(roomHistory.getMemberList())
					);

					roomHistory.setMemberList(roomHistory.getMemberList());

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
							.filter(recordFiles -> recordFiles.getSessionId().equals(roomHistory.getSessionId()))
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
							.filter(recordFiles -> recordFiles.getSessionId().equals(roomHistory.getSessionId()))
							.count()
					);

					/*roomHistory.setServerRecord(
						serverRecFileAll
							.stream()
							.count()
					);
					roomHistory.setLocalRecord(
						localRecFileAll
							.stream()
							.count()
					);
					roomHistory.setAttach(
						attachFileAll
							.stream()
							.count()
					);*/

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

				// 페이징 사용시
				if (request.isPaging()) {
					int currentPageNumber = request.getPage() + 1; // current page number (start : 0)
					int pagingSize = request.getSize(); // page data count
					long totalElements = roomHistories.size(); // return searched data count
					int totalPageNumber = totalElements % pagingSize == 0 ? (int)(totalElements / (pagingSize)) :
						(int)(totalElements / (pagingSize)) + 1;
					boolean last = (currentPageNumber) == totalPageNumber;
					int startIndex = 0;
					int endIndex = 0;

					if (!roomHistories.isEmpty()) {
						startIndex = (currentPageNumber - 1) * pagingSize;
						endIndex = last ? roomHistories.size() : ((currentPageNumber - 1) * pagingSize) + (pagingSize);
					}

					// 데이터 range
					roomHistories = IntStream
						.range(startIndex, endIndex)
						.mapToObj(roomHistories::get)
						.collect(Collectors.toList());

					// 페이징 데이터 설정
					pageMeta = PageMetadataResponse.builder()
						.currentPage(currentPageNumber)
						.currentSize(pagingSize)
						.numberOfElements(roomHistories.size())
						.totalPage(totalPageNumber)
						.totalElements(totalElements)
						.last(last)
						.build();
				}
			} catch (Exception exception) {
				throw new RestServiceException(ErrorCode.ERR_ROOM_HISTORY_HANDLE);
			}
		}

		roomHistoryInfoListResponse = RoomHistoryInfoListResponse
			.builder()
			.pageMeta(pageMeta)
			.roomHistoryInfoList(roomHistories).build();

		return roomHistoryInfoListResponse;
	}

	/*public List<RoomHistoryInfoResponse> ongoingRoomMapper(
		List<Room> ongoingRooms,
		List<Member> ongoingRoomMemberHistories
	) {
		List<RoomHistoryInfoResponse> roomHistoryResponse = new ArrayList<>();
		try {
			for (Room room : ongoingRooms) {
				RoomHistoryInfoResponse roomInfoResponse = modelMapper.map(room, RoomHistoryInfoResponse.class);
				roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());
				roomInfoResponse.setStatus(false);
				roomHistoryResponse.add(roomInfoResponse);
			}

			for (RoomHistoryInfoResponse roomHistoryInfoResponse : roomHistoryResponse) {

				List<Member> ongoingRoomMembers;

				ongoingRoomMembers = ongoingRoomMemberHistories.stream()
					.filter(member -> member.getSessionId().equals(roomHistoryInfoResponse.getSessionId()))
					.collect(Collectors.toList());

				List<MemberInfoResponse> memberInfoResponses = ongoingRoomMembers.stream()
					.map(member -> modelMapper.map(member, MemberInfoResponse.class))
					.collect(Collectors.toList());

				memberInfoResponses.removeIf(
					memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

				roomHistoryInfoResponse.setMemberList(memberInfoResponses);
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_MAPPER);
		}
		return roomHistoryResponse;
	}*/

	/*public List<RoomHistoryInfoResponse> endRoomMapper(
		List<RoomHistory> endRooms,
		List<MemberHistory> endRoomMemberHistories
	) {
		List<RoomHistoryInfoResponse> roomHistoryResponse = new ArrayList<>();
		try {
			for (RoomHistory roomHistory : endRooms) {
				RoomHistoryInfoResponse endRoomHistoryResponse = modelMapper.map(
					roomHistory, RoomHistoryInfoResponse.class);
				endRoomHistoryResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
				endRoomHistoryResponse.setStatus(true);
				roomHistoryResponse.add(endRoomHistoryResponse);
			}

			for (RoomHistoryInfoResponse roomHistoryInfoResponse : roomHistoryResponse) {

				List<MemberHistory> endRoomMembers = new ArrayList<>();
				endRoomMemberHistories.stream()
					.filter(
						memberHistory -> memberHistory.getSessionId().equals(roomHistoryInfoResponse.getSessionId()))
					.forEach(endRoomMembers::add);

				List<MemberInfoResponse> memberInfoResponses = endRoomMembers.stream()
					.map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
					.collect(Collectors.toList());

				memberInfoResponses.removeIf(
					memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

				roomHistoryInfoResponse.setMemberList(memberInfoResponses);
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_HISTORY_MAPPER);
		}
		return roomHistoryResponse;
	}*/

	/*public List<FileInfoResponse> getAttached1FileList(
		String workspaceId,
		boolean deleted
	) {

		List<FileInfoResponse> fileInfoResponses;
		try {

			*//*List<File> files;

			if (deleted) {
				files = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId);
			} else {
				files = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsFalse(workspaceId, sessionId);
			}*//*

			List<File> files = fileRepository.findByWorkspaceIdAndDeleted(workspaceId, deleted);

			fileInfoResponses = files.stream()
				.map(file -> modelMapper.map(file, FileInfoResponse.class))
				.collect(Collectors.toList());
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ATTACHED_FILE_FOUND);
		}

		return fileInfoResponses;
	}*/

	/**
	 * 로컬 첨부파일 목록 요청 처리
	 * @param workspaceId - 대상 Workspace Id
	 * @param deleted - 삭제 유무
	 * @return - 로컬 첨부파일 목록
	 */
	public List<FileInfoResponse> getAttachedFileList(
		String workspaceId,
		//String sessionId,
		boolean deleted
	) {

		List<FileInfoResponse> fileInfoResponses;
		try {

			/*List<File> files;

			if (deleted) {
				files = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId);
			} else {
				files = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsFalse(workspaceId, sessionId);
			}*/

			List<File> files = fileRepository.findByWorkspaceIdAndDeleted(workspaceId, deleted);

			fileInfoResponses = files.stream()
				//.map(file -> modelMapper.map(file, FileInfoResponse.class))
				.map(file -> dashboardFileInfoMapper.toDto(file))
				.collect(Collectors.toList());
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ATTACHED_FILE_FOUND);
		}

		return fileInfoResponses;
	}

	/**
	 * 로컬 녹화파일 목록 요청 처리
	 * @param workspaceId - 대상 Workspace Id
	 * @param deleted - 삭제 유무
	 * @return - 로컬 녹화파일 목록
	 */
	public List<FileDetailInfoResponse> getLocalRecordFileList(
		String workspaceId,
		boolean deleted
	) {
		List<FileDetailInfoResponse> fileDetailInfoResponses = new ArrayList<>();
		try {

			/*List<RecordFile> recordFiles;

			if (deleted) {
				recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId);
			} else {
				recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsFalse(
					workspaceId, sessionId);
			}
*/
			List<RecordFile> recordFiles = recordFileRepository.findByWorkspaceIdAndDeleted(workspaceId, deleted);

			ApiResponse<UserInfoListResponse> listResponse = userRestService.getUserInfo(false);
			UserInfoResponse userInfo = null;
			for (RecordFile recordFile : recordFiles) {
				for (UserInfoResponse response : listResponse.getData().getUserInfoList()) {
					if (response.getUuid().equals(recordFile.getUuid())) {
						userInfo = response;
					}
				}
				//FileUserInfoResponse fileUserInfoResponse = modelMapper.map(userInfo, FileUserInfoResponse.class);
				FileUserInfoResponse fileUserInfoResponse = dashboardFileUserInfoMapper.toDto(userInfo);
				//FileDetailInfoResponse fileDetailInfoResponse = modelMapper.map(recordFile, FileDetailInfoResponse.class);
				FileDetailInfoResponse fileDetailInfoResponse = dashboardRecordFileDetailMapper.toDto(recordFile);
				fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
				fileDetailInfoResponses.add(fileDetailInfoResponse);
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_LOCAL_RECORD_FILE_FOUND);
		}
		return fileDetailInfoResponses;
	}

	/*public List<FileDetailInfoResponse> getLocalRecordFileList(
		String workspaceId,
		String sessionId,
		boolean deleted
	) {
		List<FileDetailInfoResponse> fileDetailInfoResponses = new ArrayList<>();
		try {

			*//*List<RecordFile> recordFiles;

			if (deleted) {
				recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId);
			} else {
				recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsFalse(
					workspaceId, sessionId);
			}
*//*
			List<RecordFile> recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeleted(workspaceId, sessionId, deleted);

			ApiResponse<UserInfoListResponse> listResponse = userRestService.getUserInfo(false);
			UserInfoResponse userInfo = null;
			for (RecordFile recordFile : recordFiles) {
				for (UserInfoResponse response : listResponse.getData().getUserInfoList()) {
					if (response.getUuid().equals(recordFile.getUuid())) {
						userInfo = response;
					}
				}
				FileUserInfoResponse fileUserInfoResponse = modelMapper.map(userInfo, FileUserInfoResponse.class);
				FileDetailInfoResponse fileDetailInfoResponse = modelMapper.map(
					recordFile, FileDetailInfoResponse.class);
				fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
				fileDetailInfoResponses.add(fileDetailInfoResponse);
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_LOCAL_RECORD_FILE_FOUND);
		}
		return fileDetailInfoResponses;
	}*/

	/**
	 * 정렬, 글 번호 설정
	 * @param roomHistoryInfoList - 대상 데이터
	 * @param sortProperties - 정렬 속성
	 * @param sortOrder - 차순
	 * @return - Sorted Data
	 */
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

	/**
	 * 정렬에 데이터 비교
	 * @param sortOrder - 검색 시작 일자
	 * @param result - 차순
	 * @param count1 - 비교 대상 1
	 * @param count2 - 비교 대상 2
	 * @return - 비교 결과
	 */
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

	/**
	 * 현재 진행중인 내 협업 내역 정보 조회
	 * @param option - 협업 내역 정보 조회 조건
	 * @param workspaceId - 협업 대상 워크스페이스
	 * @param userId - 내 계정 고유 식별자 정보
	 * @return - 현재 진행중인 내 협업 내역 정보
	 */
	public List<RoomHistoryInfoResponse> getMyOnGoingRoomHistory(
		RoomHistoryListRequest option, String workspaceId, String userId
	) {

		List<Room> myRoomHistory = roomRepository.findRoomHistoryInWorkspaceWithDateOrSpecificUserId(
			option.getSearchStartDate(),
			option.getSearchEndDate(),
			workspaceId,
			userId
		);

		return myRoomHistory.stream()
			.map(room -> {
				//RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(room, RoomHistoryInfoResponse.class);
				RoomHistoryInfoResponse roomHistoryInfoResponse = ongoingRoomInfoMapper.toDto(room);
				roomHistoryInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

				List<MemberInfoResponse> memberInfoResponses = room.getMembers().stream()
					.filter(member -> member.getMemberStatus() != MemberStatus.EVICTED)
					.map(
						//member -> modelMapper.map(member, MemberInfoResponse.class)
						member -> dashboardMemberInfoMapper.toDto(member)
					)
					.collect(Collectors.toList());

				roomHistoryInfoResponse.setStatus(false);
				roomHistoryInfoResponse.setMemberList(memberInfoResponses);
				return roomHistoryInfoResponse;
			}).collect(Collectors.toList());
	}

	/**
	 * 종료된 내 협업 내역 정보 조회
	 * @param option - 협업 내역 정보 조회 조건
	 * @param workspaceId - 협업 대상 워크스페이스
	 * @param userId - 내 계정 고유 식별자 정보
	 * @return - 종료된 내 협업 내역 정보
	 */
	public List<RoomHistoryInfoResponse> getMyEndRoomHistory(
		RoomHistoryListRequest option, String workspaceId, String userId
	) {

		List<RoomHistory> roomHistories = roomHistoryRepository.findRoomHistoryInWorkspaceIdWithDateOrSpecificUserId(
			option.getSearchStartDate(), option.getSearchEndDate(), workspaceId, userId
		);

		return roomHistories.stream()
			.map(roomHistory -> {
				//RoomHistoryInfoResponse endRoomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
				RoomHistoryInfoResponse endRoomHistoryInfoResponse = dashboardRoomHistoryInfoMapper.toDto(roomHistory);
				endRoomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
				endRoomHistoryInfoResponse.setStatus(true);

				// MemberHistory 도메인 객체의 경우 MemberStatus 값이 없기에, 회원 정보 필터링 불가능
				List<MemberInfoResponse> memberInfoResponses = roomHistory.getMemberHistories().stream()
					.map(
						//memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class)
						memberHistory -> dashboardMemberHistoryMapper.toDto(memberHistory)
					)
					.collect(Collectors.toList());

				endRoomHistoryInfoResponse.setMemberList(memberInfoResponses);
				return endRoomHistoryInfoResponse;
		}).collect(Collectors.toList());
	}

	/**
	 * 멤버 표출 순위 설정(Leader가 1번째)
	 * @param members - 대상 멤버 리스트
	 * @return - Leader가 1번째로 변경된 멤버 목록
	 */
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
