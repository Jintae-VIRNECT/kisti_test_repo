package com.virnect.data.service;

import com.virnect.data.ApiResponse;
import com.virnect.data.dao.*;
import com.virnect.data.data.MemberHistoryRepository;
import com.virnect.data.data.MemberRepository;
import com.virnect.data.data.RoomHistoryRepository;
import com.virnect.data.data.RoomRepository;
import com.virnect.data.dto.SessionResponse;
import com.virnect.data.dto.request.JoinRoomRequest;
import com.virnect.data.dto.request.ModifyRoomInfoRequest;
import com.virnect.data.dto.request.RoomProfileUpdateRequest;
import com.virnect.data.dto.request.RoomRequest;
import com.virnect.data.dto.rpc.ClientMetaData;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class SessionService {
    private static final String TAG = SessionService.class.getSimpleName();

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final RoomHistoryRepository roomHistoryRepository;
    private final MemberHistoryRepository memberHistoryRepository;

    //===========================================  Room Services     =================================================//
    @Transactional
    public Room createRoom(RoomRequest roomRequest,
                           RoomProfileUpdateRequest roomProfileUpdateRequest,
                           @Nullable SessionResponse sessionResponse) {
        log.info("createRoom: " + roomRequest.toString());
        // Remote Room Entity Create
        Room room = Room.builder()
                .sessionId(sessionResponse.getId())
                .title(roomRequest.getTitle())
                .description(roomRequest.getDescription())
                .leaderId(roomRequest.getLeaderId())
                .workspaceId(roomRequest.getWorkspaceId())
                .build();

        // Remote Session Property Entity Create
        SessionProperty sessionProperty = SessionProperty.builder()
                .mediaMode("ROUTED")
                .recordingMode("MANUAL")
                .defaultOutputMode("COMPOSED")
                .defaultRecordingLayout("BEST_FIT")
                .recording(true)
                .room(room)
                .build();

        room.setSessionProperty(sessionProperty);

        // profile image request
        // not ready to upload
        //room.setProfile(Default.ROOM_PROFILE.getValue());
        room.setProfile("default");
        /*if(roomProfileUpdateRequest.getProfile() != null) {
            try {
                String profileUrl = this.fileUploadService.upload(roomProfileUpdateRequest.getProfile());
                remoteRoom.setProfile(profileUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            remoteRoom.setProfile(Default.ROOM_PROFILE.getValue());
        }*/
        // set room members
        if(!roomRequest.getLeaderId().isEmpty() && !roomRequest.getLeaderEmail().isEmpty()) {
            log.debug("leader Id is {}", roomRequest.getLeaderId());
            Member member = Member.builder()
                    .room(room)
                    .memberType(MemberType.LEADER)
                    .workspaceId(roomRequest.getWorkspaceId())
                    .uuid(roomRequest.getLeaderId())
                    .email(roomRequest.getLeaderEmail())
                    .sessionId(room.getSessionId())
                    .build();

            room.getMembers().add(member);
        } else {
            log.debug("leader Id is null");
        }

        if(!roomRequest.getParticipants().isEmpty()) {
            for (RoomRequest.Participant participant : roomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .memberType(MemberType.UNKNOWN)
                        .workspaceId(roomRequest.getWorkspaceId())
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();

                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }
        return roomRepository.save(room);
    }

    @Transactional
    //public void createSession(Session sessionNotActive) {
    public void createSession(String sessionId) {
        log.info("session create and sessionEventHandler is here");
        //Room room = roomRepository.findBySessionId(sessionNotActive.getSessionId()).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        //set active time
        LocalDateTime activeTime = LocalDateTime.now();
        room.setActiveDate(activeTime);
        room.setRoomStatus(RoomStatus.ACTIVE);
        roomRepository.save(room);
    }

    public Room getRoom(String workspaceId, String sessionId) {
        return  this.roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
    }

    public List<Room> getRoomList(String workspaceId, String userId) {
        List<Room> roomList = new ArrayList<>();
        List<Member> memberList;
        memberList = this.memberRepository.findByWorkspaceIdAndUuidAndRoomNotNull(workspaceId, userId);
        for(Member member : memberList ) {
            if(member.getRoom().getRoomStatus().equals(RoomStatus.ACTIVE)) {
                roomList.add(member.getRoom());
            }
        }
        return roomList;
    }

    public List<Room> getRoomList(Page<Member> memberPage) {
        List<Room> roomList = new ArrayList<>();
        for(Member member : memberPage.getContent() ) {
            if(member.getRoom().getRoomStatus().equals(RoomStatus.ACTIVE)) {
                roomList.add(member.getRoom());
            }
        }
        return roomList;
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
        return this.memberRepository.findByWorkspaceIdAndUuidAndRoomNotNull(workspaceId, userId, pageable);
    }

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
                    .build();

            // Set room member history
            // Get Member List by Room Session Ids
            List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
            // Mapping Member List Data to Member History List
            for (Member member : memberList) {
                MemberHistory memberHistory = MemberHistory.builder()
                        .roomHistory(roomHistory)
                        .workspaceId(member.getWorkspaceId())
                        .uuid(member.getUuid())
                        .email(member.getEmail())
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
    public void removeRoom(Room room) {
        log.info("ROOM INFO DELETE => []");
        // Room and Member History 저장
        // Remote Room History Entity Create
        RoomHistory roomHistory = RoomHistory.builder()
                .sessionId(room.getSessionId())
                .title(room.getTitle())
                .description(room.getDescription())
                .profile(room.getProfile())
                .leaderId(room.getLeaderId())
                .workspaceId(room.getWorkspaceId())
                .build();

        // Set room member history
        // Get Member List by Room Session Ids
        List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
        // Mapping Member List Data to Member History List
        for (Member roomMember : memberList) {
            MemberHistory memberHistory = MemberHistory.builder()
                    .roomHistory(roomHistory)
                    .workspaceId(roomMember.getWorkspaceId())
                    .uuid(roomMember.getUuid())
                    .email(roomMember.getEmail())
                    .memberType(roomMember.getMemberType())
                    .deviceType(roomMember.getDeviceType())
                    .sessionId(roomMember.getSessionId())
                    .startDate(roomMember.getStartDate())
                    .endDate(roomMember.getEndDate())
                    .durationSec(roomMember.getDurationSec())
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

    @Transactional
    public void destroySession(String sessionId) {
        Room room = roomRepository.findBySessionId(sessionId).orElse(null);
        if(room == null) {
            log.info("session destroy and sessionEventHandler is faild. room data is null");
        } else {
            // Room and Member History 저장
            // Remote Room History Entity Create
            RoomHistory roomHistory = RoomHistory.builder()
                    .sessionId(room.getSessionId())
                    .title(room.getTitle())
                    .description(room.getDescription())
                    .profile(room.getProfile())
                    .leaderId(room.getLeaderId())
                    .workspaceId(room.getWorkspaceId())
                    .build();

            // Set room member history
            // Get Member List by Room Session Ids
            List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
            // Mapping Member List Data to Member History List
            for (Member member : memberList) {
                MemberHistory memberHistory = MemberHistory.builder()
                        .roomHistory(roomHistory)
                        .workspaceId(member.getWorkspaceId())
                        .uuid(member.getUuid())
                        .email(member.getEmail())
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
    }

    @Transactional
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
    }

    @Transactional
    public void joinSession(String sessionId, ClientMetaData clientMetaData) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        for (Member member:room.getMembers()) {
            if(member.getUuid().equals(clientMetaData.getClientData())) {
                member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                member.setMemberStatus(MemberStatus.LOAD);
            }
        }

        roomRepository.save(room);
    }

    /**
     * This method is not for leader user
     * @param room
     * @param member
     * @return
     */
    @Transactional
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
    }

    @Transactional
    public void leaveSession(String sessionId, ClientMetaData clientMetaData) {
        //Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        Room room = roomRepository.findBySessionId(sessionId).orElse(null);
        if (room == null) {
            log.info("session leave and sessionEventHandler is faild. room data is null");
        } else {
            if (room.getMembers().isEmpty()) {
                log.info("session leave and sessionEventHandler is here: room members empty");
            } else {
                for (Member member : room.getMembers()) {
                    if (member.getUuid().equals(clientMetaData.getClientData())) {
                        member.setMemberStatus(MemberStatus.UNLOAD);
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
                //log.info("session leave and sessionEventHandler is here: room members not found");
            }
        }
    }

    @Transactional
    public Room modifyRoomInfo(Room room, ModifyRoomInfoRequest modifyRoomInfoRequest, RoomProfileUpdateRequest roomProfileUpdateRequest) {
        log.info("ROOM INFO UPDATE BY SESSION ID => [{}]");

        room.setTitle(modifyRoomInfoRequest.getTitle());
        room.setDescription(modifyRoomInfoRequest.getDescription());

        // new profile image
        //room.setProfile(Default.ROOM_PROFILE.getValue());
        room.setProfile("default");
        /*if(roomProfileUpdateRequest.getProfile() != null) {
            try {
                String profileUrl = this.fileUploadService.upload(roomProfileUpdateRequest.getProfile());
                this.fileUploadService.delete(room.getProfile());
                room.setProfile(profileUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            room.setProfile(Default.ROOM_PROFILE.getValue());
        }*/
        return this.roomRepository.save(room);
    }

    @Transactional
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
