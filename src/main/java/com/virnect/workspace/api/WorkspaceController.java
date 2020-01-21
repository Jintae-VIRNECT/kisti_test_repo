package com.virnect.workspace.api;

import com.virnect.workspace.application.WorkspaceService;
import com.virnect.workspace.dto.WorkspaceDTO;
import com.virnect.workspace.exception.BusinessException;
import com.virnect.workspace.global.common.ResponseMessage;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<ResponseMessage> createWorkspace(@RequestBody @Valid WorkspaceDTO.WorkspaceInfo WorkspaceInfo, BindingResult result) {
        log.info("createWorkspace : {}", WorkspaceInfo);
        if (result.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        } else {
            ResponseMessage responseMessage = this.workspaceService.createWorkspace(WorkspaceInfo);
            return ResponseEntity.ok(responseMessage);
        }
    }

    @GetMapping("{userId}")
    public ResponseEntity<ResponseMessage> getUserWorkspaces(@PathVariable("userId") String userId) {
        log.info("getWorkspace : {}", userId);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.workspaceService.getUserWorkspaces(userId);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("{workspaceId}/members/{userId}")
    public ResponseEntity<ResponseMessage> getMember(@PathVariable("workspaceId") long workspaceId, @PathVariable("userId") String userId, @RequestParam("search") String search, @RequestParam("filter") String filter, @RequestParam("align") String align) {
        if (!StringUtils.hasText(userId) || workspaceId <= 0) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.workspaceService.getMember(workspaceId, userId, search, filter, align);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("test")
    public ResponseEntity<ResponseMessage> getTest(@RequestParam("uuid") String uuid, @RequestParam("search") String search) {
        ResponseMessage responseMessage = this.workspaceService.getUserWorkspaces(uuid);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("{workspaceId}/permission/master_id")
    public ResponseEntity getWorkspacePermission() {
        return ResponseEntity.ok(200);
    }

    @PostMapping("{workspaceId}/permission")
    public ResponseEntity setWorkspacePermission() {
        return ResponseEntity.ok(200);
    }

}
