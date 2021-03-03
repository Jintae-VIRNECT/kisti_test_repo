package com.virnect.serviceserver.serviceremote.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.serviceserver.serviceremote.dto.request.room.RoomHistoryDeleteRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.RoomRequest;
import com.virnect.serviceserver.serviceremote.application.HistoryService;
import com.virnect.serviceserver.serviceremote.application.RoomService;
import com.virnect.serviceserver.serviceremote.dto.response.PageRequest;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomHistoryInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class HistoryRestController {

    private static final String TAG = HistoryRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/history";

    private final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    private final RoomService roomService;
    private final HistoryService historyService;

    @ApiOperation(value = "Load Room History Information List", notes = "최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
        @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
        @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "endDate, desc"),
    })
    @GetMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryListCurrent(
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "paging") boolean paging,
        @ApiIgnore PageRequest pageable
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "userId:" + (userId != null ? userId : "{}"),
            "getHistoryListCurrent"
        );

        RoomHistoryInfoListResponse responseData = historyService.getRoomHistoryCurrent(
            workspaceId, userId, paging, pageable.ofSortBy());

        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    @ApiOperation(value = "Search Room History Information List", notes = "검색 기준으로 최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
        @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate, desc"),
    })
    @GetMapping(value = "history/search")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryListStandardSearch(
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "search", required = false) String search,
        @ApiIgnore PageRequest pageable
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "userId:" + (userId != null ? userId : "{}") + "/"
                + "search:" + (search != null ? search : "{}"),
            "getHistoryListStandardSearch"
        );

        RoomHistoryInfoListResponse responseData = historyService.getHistoryListStandardSearch(
            workspaceId, userId, search, pageable.ofSortBy());

        return ResponseEntity.ok(new ApiResponse<>(responseData));

    }

    @ApiOperation(value = "Redial a History Remote Room with Company Code", notes = "This api will be deprecated")
    @PostMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequest(
        @RequestBody @Valid RoomRequest roomRequest,
        @RequestParam(name = "sessionId") String sessionId,
        @RequestParam(name = "companyCode") int companyCode,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + (roomRequest.toString() != null ? roomRequest.toString() : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "companyCode:" + companyCode,
            "redialRoomRequest"
        );

        // check room request handler
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "redialRoomRequest",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomResponse> responseData = roomService.redialRoomRequest(
            roomRequest,
            sessionId,
            companyCode
        );

        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Redial a History Remote Room with Company Code", notes = "Redial Remote Session")
    @PostMapping(value = "history/{userId}")
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequestByUserId(
        @RequestHeader(name = "client", required = false) String client,
        @PathVariable(name = "userId") String userId,
        @RequestBody @Valid RoomRequest roomRequest,
        @RequestParam(name = "sessionId") String sessionId,
        @RequestParam(name = "companyCode") int companyCode,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + (roomRequest.toString() != null ? roomRequest.toString() : "{}") + "/"
                + "client:" + (client != null ? client : "{}") + "/"
                + "userId:" + (userId != null ? userId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "companyCode:" + companyCode,
            "redialRoomRequestByUserId"
        );
        // check room request handler
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "redialRoomRequestHandler",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomResponse> responseData = roomService.redialRoomRequestByUserId(
            client,
            userId,
            roomRequest,
            sessionId,
            companyCode
        );

        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryByWorkspaceIdAndSessionId(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (sessionId != null ? sessionId : "{}"),
            "getHistoryByWorkspaceIdAndSessionId"
        );

        if ((workspaceId == null || workspaceId.isEmpty()) ||
            (sessionId == null || sessionId.isEmpty())) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomHistoryDetailInfoResponse> responseData = historyService.getHistoryBySessionId(workspaceId, sessionId);

        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryByWorkspaceIdAndUserId(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("userId") String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (userId != null ? userId : "{}"),
            "deleteHistoryByWorkspaceIdAndUserId"
        );
        if ((workspaceId == null || workspaceId.isEmpty()) ||
            (userId == null || userId.isEmpty())) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> responseData = historyService.deleteHistory(workspaceId, userId);

        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryByWorkspaceId(
        @PathVariable("workspaceId") String workspaceId,
        @RequestBody @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "::"
                + (roomHistoryDeleteRequest.toString() != null ? roomHistoryDeleteRequest.toString() : "{}"),
            "deleteHistoryByWorkspaceId"
        );
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> responseData = historyService.deleteHistoryById(
            workspaceId,
            roomHistoryDeleteRequest
        );

        return ResponseEntity.ok(responseData);
    }
}