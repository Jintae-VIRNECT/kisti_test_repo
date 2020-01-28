package com.virnect.workspace.api;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.WorkspaceDTO;
import com.virnect.workspace.exception.BusinessException;
import com.virnect.workspace.global.common.ResponseMessage;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: workspace service rest api controller
 */
@Slf4j
@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    /**
     * 워크스페이스 생성(only master user)
     *
     * @param WorkspaceInfo - 워크스페이스 정보(userId, description)
     * @param result        - 생성된 워크스페이스 정보
     * @return
     */
    @PostMapping
    public ResponseEntity<ResponseMessage> createWorkspace(@RequestBody @Valid WorkspaceDTO.WorkspaceInfo WorkspaceInfo, BindingResult result) {
        log.info("createWorkspace : {}", WorkspaceInfo);
        if (result.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.workspaceService.createWorkspace(WorkspaceInfo);
        return ResponseEntity.ok(responseMessage);

    }

    /**
     * 사용자가 속한 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseMessage> getUserWorkspaces(@PathVariable("userId") String userId) {
        log.info("getWorkspace : {}", userId);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.workspaceService.getUserWorkspaces(userId);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 사용자 조회(use 검색, 필터, 정렬 기능)
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId      - 사용자 uuid
     * @param search      - 검색명
     * @param filter      - 필터명(all USER or MASTER USER)
     * @param align       - 정렬방식(asc or desc)
     * @return
     */


    /**
     * 사용자 조회(use 검색, 필터, 정렬 기능)
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId - 사용자 uuid
     * @param search - 검색명
     * @param filter - 필터명 (all User or Master User)
     * @param pageable - 페이징 + 정렬(page : 몇페이지를 보여줄지(default 0), size : 한 페이지에 몇개 보여줄건지(default 20), sort : 뭐를 기준으로 어떻게 정렬할건지)
     * @return
     */
    @GetMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<ResponseMessage> getMember(@PathVariable("workspaceId") String workspaceId, @PathVariable("userId") String userId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) String filter, Pageable pageable) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(workspaceId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.workspaceService.getMember(workspaceId, userId, search, filter, pageable);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 워크스페이스 정보 조회(only master user)
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId      - 사용자 uuid
     * @return
     */
    @GetMapping("/{workspaceId}/{userId}")
    public ResponseEntity<ResponseMessage> getWorkspaceInfo(@PathVariable("workspaceId") String workspaceId, @PathVariable("userId") String userId) {
        if (!StringUtils.hasText(workspaceId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_VALUE);
        }
        ResponseMessage responseMessage = this.workspaceService.getWorkspaceInfo(workspaceId, userId);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/{workspaceId}/permission/master_id")
    public ResponseEntity getWorkspacePermission() {
        return ResponseEntity.ok(200);
    }

    @PostMapping("/{workspaceId}/permission")
    public ResponseEntity setWorkspacePermission() {
        return ResponseEntity.ok(200);
    }

}
