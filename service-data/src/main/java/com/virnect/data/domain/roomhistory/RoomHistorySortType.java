package com.virnect.data.domain.roomhistory;

public enum RoomHistorySortType {
	NO,	// 글 번호
	TITLE,	// 협업명
	LEADER_NICK_NAME,	// 리더 닉네임
	ACTIVE_DATE,		// 협업 시작일
	STATUS,	// 협업 상태 (전체, 진행중, 종료)
	SERVER_RECORD_FILE_COUNT, // 서버 녹화 파일 개수
	LOCAL_RECORD_FILE_COUNT,  // 로컥 녹화 파일 개수
	ATTACHED_FILE_COUNT  // 첨부 파일 개수
}
