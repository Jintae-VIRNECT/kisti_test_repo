package com.virnect.workspace.api.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.application.workspaceuser.custom.WorkspaceUserServiceCustom;
import com.virnect.workspace.dto.response.WorkspaceUserInfoListResponse;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.error.ErrorCode;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/workspaces")
public class WorkspaceUserControllerCustom {

	private final WorkspaceUserServiceCustom workspaceUserServiceCustom;

	@ApiOperation(
		value = "워크스페이스 대쉬보드 히스토리 멤버 조회",
		notes = "워크스페이스 대쉬보드 히스토리를 위한 유저 리스트."
	)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 uuid", dataType = "string", defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8", paramType = "path", required = true),
	})
	@GetMapping("/{workspaceId}/members/simple/custom")
	public ResponseEntity<ApiResponse<WorkspaceUserInfoListResponse>> getSimpleWorkspaceUserList(
		@PathVariable("workspaceId") String workspaceId
	) {

		log.info("custom get Simple workspace User List >>>>> ");
		if (!StringUtils.hasText(workspaceId)) {
			throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		WorkspaceUserInfoListResponse response = workspaceUserServiceCustom.getSimpleWorkspaceUserList(workspaceId);
		return ResponseEntity.ok(new ApiResponse<>(response));
	}

}