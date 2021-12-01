package com.virnect.process.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.process.domain.Issue;
import com.virnect.process.domain.Job;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.21
 */
public interface IssueCustomRepository {

	Long countIssuesInSubProcess(Long subProcessId);

	Page<Issue> getTroubleMemo(String userUUID, Pageable pageable);

	Page<Issue> getTroubleMemoSearchUserName(String userUUID, List<String> userUUIDList, Pageable pageable);

	//
	//    Page<Issue> getIssuesInSearchUserName(String workspaceUUID, List<String> userUUIDList, Pageable pageable);
	//
	Page<Issue> getIssuesIn(
		String userUUID, String workspaceUUID, String search, Long stepId, List<String> userUUIDList, Pageable pageable
	);

	Page<Issue> getIssuesInSearchProcessTitle(String userUUID, String workspaceUUID, String title, Pageable pageable);

	Page<Issue> getIssuesInSearchSubProcessTitle(
		String userUUID, String workspaceUUID, String title, Pageable pageable
	);

	Page<Issue> getIssuesInSearchJobTitle(String userUUID, String workspaceUUID, String title, Pageable pageable);

	long deleteAllIssueByJobList(List<Job> jobList);

	long deleteAllIssueByUserUUID(String userUUID);

	Page<Issue> getNonJobIssuesByUserUUIDListAndWorkspaceUUID(
		List<String> userUUIDList, String workspaceUUID, Pageable pageable
	);

	Page<Issue> getNonJobIssuesByUserUUIDAndWorkspaceUUIDAndSearch(
		String userUUID, String workspaceUUID, String search, Pageable pageable
	);
}
