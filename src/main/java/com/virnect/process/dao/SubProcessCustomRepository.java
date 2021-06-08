package com.virnect.process.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.YesOrNo;

public interface SubProcessCustomRepository {
	Page<SubProcess> getMyWorks(String workerUUID, /*long processId, String search, */Pageable pageable);

	boolean existsByIsRecentAndWorkerUUIDAndWorkspaceUUID(YesOrNo newWork, String workspaceUUID, String workerUUID);

	Page<SubProcess> getSubProcessPage(
		String workspaceUUID, Long processId, String search, List<String> userUUIDList, String userUUID,
		Pageable pageable
	);

	Page<SubProcess> getFilteredSubProcessPage(
		String workspaceUUID, Long processId, String search, List<String> userUUIDList, Pageable pageable,
		List<Conditions> filter,
		String myUUID
	);

	LocalDateTime getLastestReportedTime(String workspaceUUID, String userUUID);

	List<SubProcess> getSubProcessList(String workspaceUUID, String userUUID);

	Page<SubProcess> getMyWorksInProcess(
		String workspaceUUID, String workerUUID, Long processId, String search, Pageable pageable, String targetType
	);
}
