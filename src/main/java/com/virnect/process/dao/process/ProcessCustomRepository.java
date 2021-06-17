package com.virnect.process.dao.process;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;

public interface ProcessCustomRepository {
	/**
	 * 상태와 타겟데이터로 작업 찾기
	 * @param targetData    타겟 데이터
	 * @param state         작업 상태
	 * @return
	 */
	Optional<Process> findByTargetDataAndState(String targetData, State state);

	/**
	 * 상태로 작업 찾기
	 * @param state 작업 상태
	 * @return 작업목록
	 */
	List<Process> findByState(State state);
	
	/**
	 * 상태와 컨텐츠 식별자로 작업 찾기
	 * @param targetData
	 * @param state
	 * @return
	 */
	Optional<Process> findByContentUUIDAndStatus(String contentUUID, State state);

	/**
	 *
	 * @param workspaceUUID 워크스페이스UUID
	 * @param title         작업명
	 * @param pageable
	 * @return
	 */
	Page<Process> getProcessPageSearchUser(
		List<Conditions> filterList, String workspaceUUID, String search, List<String> userUUIDList, Pageable pageable,
		String targetType
	);

	/**
	 * 특정 작업의 Issue 카운트
	 * @param processId 작업ID
	 * @return
	 */
	int getCountIssuesInProcess(Long processId);

	/**
	 * 종료되지 않은 작업 조회
	 * @param workspaceUUID 워크스페이스UUID
	 * @param targetData    타겟데이터
	 * @return
	 */
	Optional<Process> getProcessUnClosed(String workspaceUUID, String targetData);

	Page<Process> getMyTask(
		List<Conditions> filterList, String myUUID, String workspaceUUID, String title, Pageable pageable,
		String targetType
	);

	List<Process> findByWorkspaceUUIDAndTargetType(String workspaceUUID, String targetType);
}