package com.virnect.serviceserver.serviceremote.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.company.CompanyRepository;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.Company;
import com.virnect.data.domain.DeviceType;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.domain.session.SessionProperty;
import com.virnect.data.domain.session.SessionPropertyHistory;
import com.virnect.data.domain.session.SessionType;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.serviceserver.serviceremote.dto.request.room.InviteRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.JoinRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.serviceserver.serviceremote.dto.response.rpc.ClientMetaData;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionTransactionalService {

	private final ModelMapper modelMapper;
	private final RoomRepository roomRepository;
	private final MemberRepository memberRepository;
	private final RoomHistoryRepository roomHistoryRepository;
	private final MemberHistoryRepository memberHistoryRepository;
	private final CompanyRepository companyRepository;

	@Transactional
	public Company createCompany(Company company) {
		return companyRepository.save(company);
	}
	//===========================================  Admin Services     =================================================//
	@Transactional
	public Company updateCompany(Company company) {
		return companyRepository.save(company);
	}

	public Company getCompany(String workspaceId) {
		return companyRepository.findByWorkspaceId(workspaceId).orElse(null);
	}

	//===========================================  Room Services     =================================================//
	@Transactional
	public Room createRoom(Room room) {
		return roomRepository.save(room);
	}

	@Transactional
	public void createSession(String sessionId) {
		log.info("session create and sessionEventHandler is here");
		Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		//set active time
		LocalDateTime activeTime = LocalDateTime.now();
		room.setActiveDate(activeTime);
		room.setRoomStatus(RoomStatus.ACTIVE);
		roomRepository.save(room);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Room getRoomForWrite(String workspaceId, String sessionId) {
		return this.roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
	}

	public Optional<Room> getRoom(String workspaceId, String sessionId) {
		//return  this.roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
		return this.roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId);
	}

	public Room getRoom(String sessionId) {
		return  this.roomRepository.findBySessionId(sessionId).orElse(null);
	}

	public RoomHistory getRoomHistory(String workspaceId, String sessionId) {
		return this.roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
	}

	public RoomHistory getRoomHistory(String sessionId) {
		return this.roomHistoryRepository.findBySessionId(sessionId).orElse(null);
	}

	/**
	 *
	 * @param workspaceId
	 * @param userId
	 * @return
	 */
	public List<Room> getRoomList(String workspaceId, String userId) {
		List<Room> roomList = new ArrayList<>();
		List<Member> memberList;
		memberList = this.memberRepository.findByWorkspaceIdAndUuidAndRoomNotNull(workspaceId, userId);
		for(Member member : memberList ) {
			//member status condition added
			if (member.getRoom().getRoomStatus().equals(RoomStatus.ACTIVE)
				&& !member.getRoom().getSessionProperty().getSessionType().equals(SessionType.OPEN)
				&& !member.getMemberStatus().equals(MemberStatus.EVICTED)) {
				roomList.add(member.getRoom());
			}
		}
		return roomList;
	}

	@Deprecated
	public Page<Room> getRoomPageList(String workspaceId, Pageable pageable) {
		return this.roomRepository.findRoomByWorkspaceId(workspaceId, pageable);
	}

	public Page<Room> getRoomPageList(String workspaceId, String userId, Pageable pageable) {
		//return this.roomRepository.findAll(joinMember(workspaceId, userId), pageable);
		return this.roomRepository.findMyRoomSpecificUserId(workspaceId, userId, true, pageable);
	}

	public Page<Room> getRoomPageList(String workspaceId, String userId, String search, Pageable pageable) {
		//return this.roomRepository.findAll(joinMember(workspaceId, userId, search), pageable);
		return this.roomRepository.findRoomBySearch(workspaceId, userId, search, pageable);
	}

	public Page<Room> getRoomPageList(String workspaceId, String userId, List<String> userIds, String search, Pageable pageable) {
		//return this.roomRepository.findAll(joinMember(workspaceId, userId, userIds, search), pageable);
		return this.roomRepository.findRoomBySearch(workspaceId, userId, userIds, search, pageable);
	}

	public List<Room> getRoomList() {
		return this.roomRepository.findAll();
	}

	public List<Room> getRoomList(String workspaceId) {
		List<Room> roomList = new ArrayList<>();
		for(Room room : this.roomRepository.findByWorkspaceId(workspaceId)) {
			if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)
				&& room.getRoomStatus().equals(RoomStatus.ACTIVE)) {
				roomList.add(room);
			}
		}

		return roomList;
	}

	public List<Room> getRoomList(Page<Member> memberPage) {
		List<Room> roomList = new ArrayList<>();
		for(Member member : memberPage.getContent() ) {
			//member status condition added
			if(member.getRoom().getRoomStatus().equals(RoomStatus.ACTIVE)
				&& !member.getRoom().getSessionProperty().getSessionType().equals(SessionType.OPEN)
				&& !member.getMemberStatus().equals(MemberStatus.EVICTED)) {
				roomList.add(member.getRoom());
			}
		}
		return roomList;
	}

	@Transactional
	public void setRoom(Room room) {
		this.roomRepository.save(room);
	}

	@Transactional
	public void setRoomHistory(RoomHistory roomHistory) {
		boolean result = this.roomHistoryRepository.existsByWorkspaceIdAndSessionId(roomHistory.getWorkspaceId(), roomHistory.getSessionId());
		if(result) {
			log.error("Duplicate entry {}", roomHistory.getSessionId());
		} else {
			this.roomHistoryRepository.save(roomHistory);
		}
	}

	@Transactional
	public void setMember(Member member) {
		this.memberRepository.save(member);
	}

	/*@Transactional
    public void setMember(String workspaceId, String sessionId, String uuid, MemberStatus memberStatus) {
        Member member = this.memberRepository.findByWorkspaceIdAndSessionIdAndUuidForWrite(workspaceId, sessionId, uuid).orElse(null);
        if(member != null) {
            member.setMemberStatus(memberStatus);
            this.memberRepository.save(member);
        }
    }*/

	@Transactional
	public void setMemberHistory(MemberHistory memberHistory) {
		this.memberHistoryRepository.save(memberHistory);
	}

	@Transactional
	public Member getMemberForWrite(String workspaceId, String sessionId, String userId) {
		return memberRepository.findByWorkspaceIdAndSessionIdAndUuidForWrite(workspaceId, sessionId, userId).orElse(null);
	}

	public Member getMember(String workspaceId, String sessionId, String userId) {
		return memberRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, userId).orElse(null);
	}

	public List<Member> getMemberList(String sessionId) {
		return this.memberRepository.findAllBySessionId(sessionId);
	}

	public List<Member> getMemberList(String workspaceId, String sessionId) {
		return this.memberRepository.findByWorkspaceIdAndSessionIdAndRoomNotNull(workspaceId, sessionId);
	}

	public Page<Member> getMemberList(String workspaceId, String userId, Pageable pageable) {
		return this.memberRepository.findMemberByWorkspaceIdAndUuidAndRoomNotNull(workspaceId, userId, pageable);
	}

	public List<MemberHistory> getMemberHistoryList(String userId) {
		return this.memberHistoryRepository.findAllByUuid(userId);
	}

	@Deprecated
	@Transactional
	public ApiResponse<Boolean> removeAllRoom(String workspaceId) {
		log.info("ROOM INFO DELETE BY SESSION ID => [{}]", workspaceId);
		// Get Specific Room using Session ID
		List<Room> roomList = roomRepository.findByWorkspaceId(workspaceId);

		for (Room room: roomList) {
			RoomHistory roomHistory = RoomHistory.builder()
				.sessionId(room.getSessionId())
				.title(room.getTitle())
				.description(room.getDescription())
				.profile(room.getProfile())
				.leaderId(room.getLeaderId())
				.workspaceId(room.getWorkspaceId())
				.maxUserCount(room.getMaxUserCount())
				.licenseName(room.getLicenseName())
				.build();

			// Remote Session Property Entity Create
			SessionProperty sessionProperty = room.getSessionProperty();
			SessionPropertyHistory sessionPropertyHistory = SessionPropertyHistory.builder()
				.mediaMode(sessionProperty.getMediaMode())
				.recordingMode(sessionProperty.getRecordingMode())
				.defaultOutputMode(sessionProperty.getDefaultOutputMode())
				.defaultRecordingLayout(sessionProperty.getDefaultRecordingLayout())
				.recording(sessionProperty.isRecording())
				.keepalive(sessionProperty.isKeepalive())
				.sessionType(sessionProperty.getSessionType())
				.roomHistory(roomHistory)
				.build();

			roomHistory.setSessionPropertyHistory(sessionPropertyHistory);


			// Set room member history
			// Get Member List by Room Session Ids
			List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
			// Mapping Member List Data to Member History List
			for (Member member : memberList) {
				MemberHistory memberHistory = MemberHistory.builder()
					.roomHistory(roomHistory)
					.workspaceId(member.getWorkspaceId())
					.uuid(member.getUuid())
					.memberType(member.getMemberType())
					.deviceType(member.getDeviceType())
					.sessionId(member.getSessionId())
					.startDate(member.getStartDate())
					.endDate(member.getEndDate())
					.durationSec(member.getDurationSec())
					.build();
				memberHistoryRepository.save(memberHistory);
				roomHistory.getMemberHistories().add(memberHistory);
			}


			//set active time
			roomHistory.setActiveDate(room.getActiveDate());

			//set un active  time
			LocalDateTime endTime = LocalDateTime.now();
			roomHistory.setUnactiveDate(endTime);

			//time diff seconds
			Duration duration = Duration.between(room.getActiveDate(), endTime);
			roomHistory.setDurationSec(duration.getSeconds());

			//save room history
			roomHistoryRepository.save(roomHistory);

			//delete room
			roomRepository.delete(room);
		}

		return new ApiResponse<>(true);
	}

	@Transactional
	@Deprecated
	public void removeRoom(String workspaceId, String sessionId) {
		log.info("ROOM INFO REMOVE BY removeRoom => [{}]");
		Room room = getRoom(workspaceId, sessionId).orElse(null);
		//List<Member> members = sessionService.getMemberList(room.getWorkspaceId(), room.getSessionId());
		List<Member> members = Objects.requireNonNull(room).getMembers()
			.stream()
			.map(member -> modelMapper.map(member, Member.class))
			.collect(Collectors.toList());
		for (Member member: members) {
			log.info("ROOM INFO REMOVE BY removeRoom => [{}]", member.getUuid());
		}
	}

	@Transactional
	public void deleteRoom(Room room) {
		//roomRepository.deleteByWorkspaceIdAndSessionId(room.getWorkspaceId(), room.getSessionId());
		roomRepository.delete(room);
	}

	@Transactional
	@Deprecated
	public void destroySession(String sessionId) {
		Room room = roomRepository.findBySessionId(sessionId).orElse(null);
		if(room == null) {
			log.info("session destroy and sessionEventHandler is faild. room data is null");
		} else {
			// check the same session id history room is already exist
			RoomHistory oldRoomHistory = roomHistoryRepository.findBySessionId(room.getSessionId()).orElse(null);
			if(oldRoomHistory != null) {
				log.info("FOUND THE SAME SESSION ID => [{}]", oldRoomHistory.getSessionId());

				oldRoomHistory.setTitle(room.getTitle());
				oldRoomHistory.setDescription(room.getDescription());
				oldRoomHistory.setProfile(room.getProfile());
				oldRoomHistory.setMaxUserCount(room.getMaxUserCount());
				oldRoomHistory.setLicenseName(room.getLicenseName());

				// Remote Session Property Entity Create
				SessionProperty sessionProperty = room.getSessionProperty();
				SessionPropertyHistory sessionPropertyHistory = oldRoomHistory.getSessionPropertyHistory();
				sessionPropertyHistory.setMediaMode(sessionProperty.getMediaMode());
				sessionPropertyHistory.setRecordingMode(sessionProperty.getRecordingMode());
				sessionPropertyHistory.setDefaultOutputMode(sessionProperty.getDefaultOutputMode());
				sessionPropertyHistory.setDefaultRecordingLayout(sessionProperty.getDefaultRecordingLayout());
				sessionPropertyHistory.setRecording(sessionProperty.isRecording());
				sessionPropertyHistory.setKeepalive(sessionProperty.isKeepalive());
				sessionPropertyHistory.setSessionType(sessionProperty.getSessionType());
				sessionPropertyHistory.setRoomHistory(oldRoomHistory);

				oldRoomHistory.setSessionPropertyHistory(sessionPropertyHistory);

				// Set room member history
				// Get Member history list and set room null
				List<MemberHistory> memberHistoryList = oldRoomHistory.getMemberHistories();
				for (MemberHistory memberHistory: memberHistoryList) {
					memberHistory.setRoomHistory(null);
					this.memberHistoryRepository.save(memberHistory);
				}

				// Get Member List by Room Session Ids
				List<Member> memberList = room.getMembers();
				//List<Member> memberList = this.getMemberList(room.getWorkspaceId(), room.getSessionId());
				// Mapping Member List Data to Member History List
				for (Member member : memberList) {
					MemberHistory memberHistory = MemberHistory.builder()
						.roomHistory(oldRoomHistory)
						.workspaceId(member.getWorkspaceId())
						.uuid(member.getUuid())
						.memberType(member.getMemberType())
						.deviceType(member.getDeviceType())
						.sessionId(member.getSessionId())
						.startDate(member.getStartDate())
						.endDate(member.getEndDate())
						.durationSec(member.getDurationSec())
						.build();
					memberHistoryRepository.save(memberHistory);
					oldRoomHistory.getMemberHistories().add(memberHistory);

					//delete member
					memberRepository.delete(member);
				}

				//set active time do not update active date
				//oldRoomHistory.setActiveDate(room.getActiveDate());

				//set un active  time
				LocalDateTime endTime = LocalDateTime.now();
				oldRoomHistory.setUnactiveDate(endTime);

				//time diff seconds
				Duration duration = Duration.between(room.getActiveDate(), endTime);
				Long totalDuration = duration.getSeconds() + oldRoomHistory.getDurationSec();
				oldRoomHistory.setDurationSec(totalDuration);

				//save room history
				roomHistoryRepository.save(oldRoomHistory);
			} else {
				// Remote Room History Entity Create
				RoomHistory roomHistory = RoomHistory.builder()
					.sessionId(room.getSessionId())
					.title(room.getTitle())
					.description(room.getDescription())
					.profile(room.getProfile())
					.leaderId(room.getLeaderId())
					.workspaceId(room.getWorkspaceId())
					.maxUserCount(room.getMaxUserCount())
					.licenseName(room.getLicenseName())
					.build();

				// Remote Session Property Entity Create
				SessionProperty sessionProperty = room.getSessionProperty();
				SessionPropertyHistory sessionPropertyHistory = SessionPropertyHistory.builder()
					.mediaMode(sessionProperty.getMediaMode())
					.recordingMode(sessionProperty.getRecordingMode())
					.defaultOutputMode(sessionProperty.getDefaultOutputMode())
					.defaultRecordingLayout(sessionProperty.getDefaultRecordingLayout())
					.recording(sessionProperty.isRecording())
					.keepalive(sessionProperty.isKeepalive())
					.sessionType(sessionProperty.getSessionType())
					.roomHistory(roomHistory)
					.build();

				roomHistory.setSessionPropertyHistory(sessionPropertyHistory);

				// Set room member history
				// Get Member List by Room Session Ids
				List<Member> memberList = room.getMembers();
				// Mapping Member List Data to Member History List
				for (Member member : memberList) {
					MemberHistory memberHistory = MemberHistory.builder()
						.roomHistory(roomHistory)
						.workspaceId(member.getWorkspaceId())
						.uuid(member.getUuid())
						.memberType(member.getMemberType())
						.deviceType(member.getDeviceType())
						.sessionId(member.getSessionId())
						.startDate(member.getStartDate())
						.endDate(member.getEndDate())
						.durationSec(member.getDurationSec())
						.build();
					memberHistoryRepository.save(memberHistory);
					roomHistory.getMemberHistories().add(memberHistory);

					//delete member
					memberRepository.delete(member);
				}

				//set active time
				roomHistory.setActiveDate(room.getActiveDate());

				//set un active  time
				LocalDateTime endTime = LocalDateTime.now();
				roomHistory.setUnactiveDate(endTime);

				//time diff seconds
				Duration duration = Duration.between(room.getActiveDate(), endTime);
				roomHistory.setDurationSec(duration.getSeconds());

				//save room history
				roomHistoryRepository.save(roomHistory);

			}
			//delete room
			roomRepository.delete(room);
		}
	}

	@Transactional
	@Deprecated
	public void joinRoom(Room room, JoinRoomRequest joinRoomRequest) {
		if (room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
			Member member = memberRepository.findByWorkspaceIdAndSessionIdAndUuid(
				room.getWorkspaceId(),
				room.getWorkspaceId(),
				joinRoomRequest.getUuid()
			).orElse(null);
			if (member == null) {
				Member newMember = Member.builder()
					.room(room)
					.memberType(joinRoomRequest.getMemberType())
					.uuid(joinRoomRequest.getUuid())
					.workspaceId(room.getWorkspaceId())
					.sessionId(room.getSessionId())
					.build();

				room.getMembers().add(newMember);
				roomRepository.save(room);
			}

			//memberRepository.save(member);

            /*for (Member member: room.getMembers()) {
                if (member.getUuid().equals(joinRoomRequest.getUuid())) {
                    log.debug("Room has member Id is {}", member.getUuid());
                    member.setMemberType(joinRoomRequest.getMemberType());
                    member.setDeviceType(joinRoomRequest.getDeviceType());
                    member.setMemberStatus(MemberStatus.LOAD);
                    //save member
                    memberRepository.save(member);
                }
            }*/
            /*for (ListIterator<Member> it = room.getMembers().listIterator(); it.hasNext(); ) {
                //Member member = it.next();
                it.next()
                log.info("NEW getParticipants Id is {}", joinRoomRequest.getUuid());
                Member member = Member.builder()
                        .room(room)
                        .memberType(joinRoomRequest.getMemberType())
                        .uuid(joinRoomRequest.getUuid())
                        .workspaceId(room.getWorkspaceId())
                        .sessionId(room.getSessionId())
                        .build();

                //member.setMemberType(joinRoomRequest.getMemberType());
                //member.setDeviceType(joinRoomRequest.getDeviceType());
                //member.setMemberStatus(MemberStatus.UNLOAD);
                //memberRepository.save(member);
                it.add(member);
            }*/
		}
		//roomRepository.save(room);
		//else {
		//Private
            /*for (Member member: room.getMembers()) {
                if(member.getUuid().equals(joinRoomRequest.getUuid())) {
                    log.debug("Room has member Id is {}", member.getUuid());
                    member.setMemberType(joinRoomRequest.getMemberType());
                    member.setDeviceType(joinRoomRequest.getDeviceType());
                    member.setMemberStatus(MemberStatus.LOAD);
                    //save member
                    memberRepository.save(member);
                }
            }*/
		//}
	}

    /*@Transactional
    public ErrorCode joinRoom(Room room, JoinRoomRequest joinRoomRequest) {
        for (Member member: room.getMembers()) {
            if(member.getUuid().equals(joinRoomRequest.getUuid())) {
                log.debug("Room has member Id is {}", member.getUuid());

                if(member.getMemberStatus().equals(MemberStatus.LOAD)) {
                    return ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED;
                }

                member.setMemberType(joinRoomRequest.getMemberType());
                member.setDeviceType(joinRoomRequest.getDeviceType());
                member.setMemberStatus(MemberStatus.LOAD);
                //save member
                memberRepository.save(member);
                //room.getMembers().add(member);
                //roomRepository.save(room);
            }
        }
        return ErrorCode.ERR_SUCCESS;
    }*/

	/**
	 * final step.
	 * @param sessionId
	 * @param connectionId
	 * @param clientMetaData
	 */
	@Transactional
	@Deprecated
	public void joinSession(String sessionId, String connectionId, ClientMetaData clientMetaData) {
		Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
		for (Member member : room.getMembers()) {
			if (member.getUuid().equals(clientMetaData.getClientData())) {
				member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
				member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
				member.setConnectionId(connectionId);
				member.setMemberStatus(MemberStatus.LOAD);
			}
		}
		roomRepository.save(room);

		//if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
            /*for(ListIterator<Member> it = room.getMembers().listIterator(); it.hasNext();) {
                Member member = it.next();

                if(member.getUuid().equals(clientMetaData.getClientData())) {
                    member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                    member.setConnectionId(connectionId);
                    member.setMemberStatus(MemberStatus.LOAD);
                } else {
                    Member newMember = Member.builder()
                            .room(room)
                            .memberType(MemberType.valueOf(clientMetaData.getRoleType()))
                            .workspaceId(room.getWorkspaceId())
                            .uuid(clientMetaData.getClientData())
                            .sessionId(room.getSessionId())
                            .build();

                    newMember.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    newMember.setConnectionId(connectionId);
                    newMember.setMemberStatus(MemberStatus.LOAD);
                    it.add(newMember);
                }
            }*/


            /*room.getMembers().stream().map(member -> {
                if(member.getUuid().equals(clientMetaData.getClientData())) {
                    member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                    member.setConnectionId(connectionId);
                    member.setMemberStatus(MemberStatus.LOAD);
                } else {
                    member = Member.builder()
                            .room(room)
                            .memberType(MemberType.valueOf(clientMetaData.getRoleType()))
                            .workspaceId(room.getWorkspaceId())
                            .uuid(clientMetaData.getClientData())
                            .sessionId(room.getSessionId())
                            .build();

                    member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    member.setConnectionId(connectionId);
                    member.setMemberStatus(MemberStatus.LOAD);
                }
                return null;
            }).collect(Collectors.toList());*/
            /*for (Member member : memberList) {
                if (member.getUuid().equals(clientMetaData.getClientData())) {
                    member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                    member.setConnectionId(connectionId);
                    member.setMemberStatus(MemberStatus.LOAD);
                } else {
                    newMember = Member.builder()
                            .room(room)
                            .memberType(MemberType.valueOf(clientMetaData.getRoleType()))
                            .workspaceId(room.getWorkspaceId())
                            .uuid(clientMetaData.getClientData())
                            .sessionId(room.getSessionId())
                            .build();

                    newMember.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    newMember.setConnectionId(connectionId);
                    newMember.setMemberStatus(MemberStatus.LOAD);
                }
            }
            if (newMember != null)
                room.getMembers().add(newMember);*/
		//} else {
            /*for (Member member : room.getMembers()) {
                if (member.getUuid().equals(clientMetaData.getClientData())) {
                    member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                    member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                    member.setConnectionId(connectionId);
                    member.setMemberStatus(MemberStatus.LOAD);
                }
            }*/
		//}
		//roomRepository.save(room);
	}

	/**
	 * This method is not for leader user
	 * @param room
	 * @param member
	 * @return
	 */
    /*@Transactional
    @Deprecated
    public ErrorCode exitRoom(Room room, Member member) {
        for (Member roomMember : room.getMembers()) {
            if (member.getUuid().equals(roomMember.getUuid())) {
                if (member.getMemberType().equals(MemberType.LEADER)) {
                    return ErrorCode.ERR_ROOM_LEADER_INVALID_EXIT;
                } else if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                    return ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED;
                } else {
                    room.getMembers().remove(roomMember);
                    roomRepository.save(room);
                    return ErrorCode.ERR_SUCCESS;
                }
            }
        }
        return ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND;
    }*/

	@Transactional
	public void removeMember(Room room, Member member) {
		room.getMembers().remove(member);
		roomRepository.save(room);
	}

	@Transactional
	@Deprecated
	public void leaveSession(String sessionId, ClientMetaData clientMetaData) {
		//Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
		Room room = roomRepository.findBySessionId(sessionId).orElse(null);
		if (room == null) {
			log.info("session leave and sessionEventHandler is faild. room data is null");
		} else {
			if (room.getMembers().isEmpty()) {
				log.info("session leave and sessionEventHandler is here: room members empty");
			} else {
				if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
					room.getMembers().removeIf(member -> member.getUuid().equals(clientMetaData.getClientData()));
					roomRepository.save(room);
				} else {
					for (Member member : room.getMembers()) {
						if (member.getUuid().equals(clientMetaData.getClientData())) {
							//set status unload
							member.setMemberStatus(MemberStatus.UNLOAD);
							//set connection id to empty
							member.setConnectionId("");
							//set end time
							LocalDateTime endTime = LocalDateTime.now();
							member.setEndDate(endTime);

							//time diff seconds
							Long totalDuration = member.getDurationSec();
							Duration duration = Duration.between(member.getStartDate(), endTime);
							member.setDurationSec(totalDuration + duration.getSeconds());

							//save member
							memberRepository.save(member);
						}
					}
				}
				//log.info("session leave and sessionEventHandler is here: room members not found");
			}
		}
	}

	@Transactional
	@Deprecated
	public void disconnectSession(String sessionId, ClientMetaData clientMetaData) {
		Room room = roomRepository.findBySessionId(sessionId).orElse(null);
		if (room == null) {
			log.info("session disconnect and sessionEventHandler is faild. room data is null");
		} else {
			if (room.getMembers().isEmpty()) {
				log.info("session disconnect and sessionEventHandler is here: room members empty");
			} else {
				log.info("session disconnect and sessionEventHandler is try to user evict by disconnection");
				for (Member member : room.getMembers()) {
					if (member.getUuid().equals(clientMetaData.getClientData())) {
						log.info("session disconnect and sessionEventHandler evict user id::{}", member.getUuid());
						//set status evicted
						member.setMemberStatus(MemberStatus.EVICTED);
						//set connection id to empty
						member.setConnectionId("");
						//set end time
						LocalDateTime endTime = LocalDateTime.now();
						member.setEndDate(endTime);

						//time diff seconds
						Long totalDuration = member.getDurationSec();
						Duration duration = Duration.between(member.getStartDate(), endTime);
						member.setDurationSec(totalDuration + duration.getSeconds());

						//set room null
						//member.setRoom(null);

						//save member
						memberRepository.save(member);
					}
				}
				//log.info("session leave and sessionEventHandler is here: room members not found");
			}
		}
	}

	@Transactional
	public Room updateRoom(Room room, InviteRoomRequest inviteRoomRequest) {
		log.info("updateRoom by InviteRoomRequest");
		for (String participant : inviteRoomRequest.getParticipantIds()) {
			log.debug("getParticipants Id is {}", participant);
			Member member = Member.builder()
				.room(room)
				.memberType(MemberType.UNKNOWN)
				.workspaceId(room.getWorkspaceId())
				.uuid(participant)
				.sessionId(room.getSessionId())
				.build();
			room.getMembers().add(member);
		}
		return roomRepository.save(room);
	}

	@Transactional
	public Room updateRoom(Room room, ModifyRoomInfoRequest modifyRoomInfoRequest) {
		return this.roomRepository.save(room);
	}

	@Transactional
	public void updateRoom(Room room, String profile) {
		log.info("generate room profile string is {}", profile);
		room.setProfile(profile);
		roomRepository.save(room);
	}


	@Transactional
	public Room updateRoom(Room room) {
		return this.roomRepository.save(room);
	}

	@Transactional
	public void updateMember(Room room, List<String> userIds) {
		for (String userId: userIds) {
			Member member = memberRepository.findByWorkspaceIdAndSessionIdAndUuid(room.getWorkspaceId(), room.getSessionId(), userId).orElse(null);
			if(member == null) {
				memberRepository.save(Member.builder()
					.room(room)
					.memberType(MemberType.UNKNOWN)
					.workspaceId(room.getWorkspaceId())
					.uuid(userId)
					.sessionId(room.getSessionId())
					.build());
			} else {
				member.setMemberStatus(MemberStatus.UNLOAD);
				member.setMemberType(MemberType.UNKNOWN);
				memberRepository.save(member);
			}
		}
	}

	/*@Transactional
    public void updateMember(Member member, MemberStatus memberStatus) {
        member.setMemberStatus(memberStatus);
        memberRepository.save(member);
    }*/

    /*@Transactional
    public void createOrUpdateMember(Room room, String userId) {
        log.debug("updateRoom member Id is {}", userId);
        for(Member member : room.getMembers()) {
            if(member.getUuid().equals(userId) && member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                member.setMemberStatus(MemberStatus.UNLOAD);
                memberRepository.save(member);
            } else {
                room.getMembers().add(
                        Member.builder()
                                .room(room)
                                .memberType(MemberType.UNKNOWN)
                                .workspaceId(room.getWorkspaceId())
                                .uuid(userId)
                                .sessionId(room.getSessionId())
                                .build()
                );
            }
        }
        roomRepository.save(room);
    }*/

	@Transactional
	public void updateMember(Member member, MemberStatus memberStatus) {
		if (memberStatus == MemberStatus.EVICTED) {//member.setRoom(null);
			member.setMemberStatus(memberStatus);
		}
		memberRepository.save(member);
	}

	@Transactional
	public void deleteMember(Member member) {
		this.memberRepository.delete(member);
	}

	@Transactional
	@Deprecated
	public ErrorCode removeMember(Room room, String userId) {
		for (Member roomMember : room.getMembers()) {
			if (roomMember.getUuid().equals(userId)) {
				//this.memberRepository.delete(member);
				room.getMembers().remove(roomMember);
				roomRepository.save(room);
				return ErrorCode.ERR_SUCCESS;
			}
		}
		return ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND;
	}

	@Transactional
	public void updateMemberHistory(MemberHistory memberHistory) {
		memberHistoryRepository.save(memberHistory);
	}



    /*@Transactional
    public ApiResponse<Boolean> inviteRoom(String workspaceId ,String sessionId, InviteRoomRequest inviteRoomRequest) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .workspaceId(workspaceId)
                        .memberType(MemberType.WORKER)
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();
                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }

        roomRepository.save(room);

        return new ApiResponse<>(true);
    }*/

    /*@Transactional
    public ApiResponse<InviteRoomResponse> inviteRoom(
            String workspaceId ,
            String sessionId,
            InviteRoomRequest inviteRoomRequest) {

        PushSendRequest pushSendRequest = new PushSendRequest();
        pushSendRequest.setWorkspaceId(workspaceId);
        pushSendRequest.setUserId(inviteRoomRequest.getLeaderId());
        pushSendRequest.setEvent(PushConstrants.SEND_PUSH_ROOM_INVITE);
        for (InviteRoomRequest.Participant participant: inviteRoomRequest.getParticipants()) {
            pushSendRequest.getTargetUserIds().add(participant.getId());
        }

        //pushSendRequest.setContents();


        ApiResponse<PushResponse> pushResponse = messageRestService.sendPush(pushSendRequest);
        log.info("getPush message {}", pushResponse.toString());

        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .workspaceId(workspaceId)
                        .memberType(MemberType.UNKNOWN)
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();
                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }
        roomRepository.save(room);

        InviteRoomResponse inviteRoomResponse = new InviteRoomResponse();
        inviteRoomResponse.setEvent(pushResponse.getData().getEvent());
        inviteRoomResponse.setWorkspaceId(pushResponse.getData().getWorkspaceId());
        inviteRoomResponse.setSessionId(sessionId);
        inviteRoomResponse.setPublished(pushResponse.getData().isPublished());
        inviteRoomResponse.setPublishDate(pushResponse.getData().getPublishDate());

        return new ApiResponse<>(inviteRoomResponse);
    }*/

    /*public ApiResponse<Boolean> inviteRoomAccept(String workspaceId, String sessionId, String userId, Boolean accept, Locale locale) {
        if(accept) {

        } else {

        }
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .workspaceId(workspaceId)
                        .memberType(MemberType.WORKER)
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();
                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }

        roomRepository.save(room);

        return new ApiResponse<>(true);
    }*/

}
