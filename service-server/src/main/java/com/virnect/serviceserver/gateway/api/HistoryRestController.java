package com.virnect.serviceserver.gateway.api;

import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.request.PageRequest;
import com.virnect.serviceserver.gateway.dto.request.RoomHistoryDeleteRequest;
import com.virnect.serviceserver.gateway.dto.response.ResultResponse;
import com.virnect.serviceserver.gateway.dto.response.RoomHistoryDetailInfoResponse;
import com.virnect.serviceserver.gateway.dto.response.RoomHistoryInfoListResponse;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class HistoryRestController {
    private static final String TAG = "HistoryRestControllers";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/history";

    private final RemoteGatewayService remoteGatewayService;


    @ApiOperation(value = "Load Room History Information List", notes = "최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "paging") boolean paging,
            @ApiIgnore PageRequest pageable
    ) {
        log.info("REST API: GET {}/{}{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.remoteGatewayService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}");
        if (sessionId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomHistoryDetailInfoResponse> apiResponse = this.remoteGatewayService.getRoomHistoryDetailInfo(workspaceId, sessionId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        if(userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.remoteGatewayService.removeAllRoomHistory(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest,
            BindingResult result) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", roomHistoryDeleteRequest.toString());

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.remoteGatewayService.removeRoomHistory(workspaceId, roomHistoryDeleteRequest);
        return ResponseEntity.ok(apiResponse);
    }

    /*@ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}", userId != null ? userId : "{}");

        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.removeRoomHistory(workspaceId, sessionId, userId);
        return ResponseEntity.ok(apiResponse);
    }*/

}
