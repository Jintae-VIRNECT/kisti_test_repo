package com.virnect.serviceserver.serviceremote.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.company.CompanyRepository;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.Company;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RemoteServiceException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionTransactionalService {

	private final RoomRepository roomRepository;
	private final MemberRepository memberRepository;
	private final RoomHistoryRepository roomHistoryRepository;
	private final CompanyRepository companyRepository;

	//===========================================  Company Services     =================================================//
	@Transactional
	public Company updateCompany(Company company) {
		return companyRepository.save(company);
	}

	@Transactional
	public Company createCompany(Company company) {
		return companyRepository.save(company);
	}

	//===========================================  Room Services     =================================================//
	@Transactional
	public Room createRoom(Room room) {
		return roomRepository.save(room);
	}

	@Transactional
	public void createSession(String sessionId) {
		log.info("session create and sessionEventHandler is here");
		Room room = roomRepository.findBySessionId(sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
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

	@Transactional
	public void setRoom(Room room) {
		this.roomRepository.save(room);
	}

	@Transactional
	public void deleteRoom(Room room) {
		roomRepository.delete(room);
	}

	@Transactional
	public Room updateRoom(Room room) {
		return this.roomRepository.save(room);
	}

	//===========================================  Room history Services     =================================================//
	@Transactional
	public void setRoomHistory(RoomHistory roomHistory) {
		boolean result = this.roomHistoryRepository.existsByWorkspaceIdAndSessionId(
			roomHistory.getWorkspaceId(), roomHistory.getSessionId());
		if (result) {
			log.error("Duplicate entry {}", roomHistory.getSessionId());
		} else {
			this.roomHistoryRepository.save(roomHistory);
		}
	}

	//===========================================  Member Services     =================================================//
	@Transactional
	public void setMember(Member member) {
		this.memberRepository.save(member);
	}

	@Transactional
	public void updateMember(Room room, List<String> userIds) {
		for (String userId : userIds) {
			Member member = memberRepository.findByWorkspaceIdAndSessionIdAndUuid(
				room.getWorkspaceId(), room.getSessionId(), userId).orElse(null);
			if (ObjectUtils.isEmpty(member)) {
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

	@Transactional
	public void updateMember(Member member, MemberStatus memberStatus) {
		if (memberStatus == MemberStatus.EVICTED) {
			member.setMemberStatus(memberStatus);
		}
		memberRepository.save(member);
	}

	@Transactional
	public void deleteMember(Member member) {
		this.memberRepository.delete(member);
	}

}
