package com.virnect.process.dao;

import com.virnect.process.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    Page<Issue> getIssuesIn(String userUUID, String workspaceUUID, List<String> userUUIDList, Pageable pageable);

    Page<Issue> getIssuesInSearchProcessTitle(String userUUID, String workspaceUUID, String title, Pageable pageable);

    Page<Issue> getIssuesInSearchSubProcessTitle(String userUUID, String workspaceUUID, String title, Pageable pageable);

    Page<Issue> getIssuesInSearchJobTitle(String userUUID, String workspaceUUID, String title, Pageable pageable);

    Page<Issue> getIssuesOut(String userUUID, List<String> workspaceUserList, Pageable pageable);
}
