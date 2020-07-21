package com.virnect.serviceserver.gateway.api;

import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.request.PageRequest;
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
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
            @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "4705cf50e6d02c59b0eef9591666e2a3", required = true),
            @ApiImplicitParam(name = "search", value = "검색어(협업, 멤버 이름 검색)", dataType = "string", allowEmptyValue = true, defaultValue = "remote"),
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "paging") boolean paging,
            @ApiIgnore PageRequest pageable
    ) {
        log.info("REST API: GET {}", REST_PATH);
        ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.remoteGatewayService.getRoomHistoryInfoList(userId, search, paging, pageable.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{sessionId}")
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(@PathVariable("sessionId") String sessionId) {
        log.info("REST API: DELETE {}/{}", REST_PATH, sessionId != null ? sessionId : "{}");
        if (sessionId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomHistoryDetailInfoResponse> apiResponse = this.remoteGatewayService.getRoomHistoryDetailInfo(sessionId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteHistory(@PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}", REST_PATH, userId != null ? userId : "{}");
        if(userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.removeAllRoomHistory(userId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteHistoryById(
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, sessionId != null ? sessionId : "{}", userId != null ? userId : "{}");

        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.removeRoomHistory(sessionId, userId);
        return ResponseEntity.ok(apiResponse);
    }

}
