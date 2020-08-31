package com.virnect.serviceserver.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.api.IHistoryRestAPI;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.RoomHistoryDeleteRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.RoomHistoryDetailInfoResponse;
import com.virnect.data.dto.response.RoomHistoryInfoListResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.service.HistoryService;
import com.virnect.serviceserver.data.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HistoryRestController implements IHistoryRestAPI {
    private static final String TAG = HistoryRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/history";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    private final DataRepository dataRepository;

    @Override
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(String workspaceId, String userId, boolean paging, PageRequest pageable) {
        log.info("REST API: GET {}/{}{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        return ResponseEntity.ok(
                this.dataRepository.loadRoomHistoryInfoList(workspaceId, userId, paging, pageable.of())
        );
        /*ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable);
        return ResponseEntity.ok(apiResponse);*/
    }

    @Override
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(String workspaceId, String sessionId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}");
        if (sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.loadRoomHistoryDetail(workspaceId, sessionId)
        );
        /*ApiResponse<RoomHistoryDetailInfoResponse> apiResponse = this.historyService.getRoomHistoryDetailInfo(workspaceId, sessionId);
        return ResponseEntity.ok(apiResponse);*/
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(String workspaceId, String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        if(userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.removeRoomHistory(workspaceId, userId)
        );
        /*ApiResponse<ResultResponse> apiResponse = this.historyService.removeAllRoomHistory(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);*/
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(String workspaceId, @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest, BindingResult result) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", roomHistoryDeleteRequest.toString());

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.removeRoomHistory(workspaceId, roomHistoryDeleteRequest)
        );

        /*ApiResponse<ResultResponse> apiResponse = this.historyService.removeRoomHistory(workspaceId, roomHistoryDeleteRequest);
        return ResponseEntity.ok(apiResponse);*/
    }

    /*@ApiOperation(value = "Load Room History Information List", notes = "최근 기록 리스트를 조회하는 API 입니다.")
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
        //ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable.of());
        ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable);
        return ResponseEntity.ok(apiResponse);
    }*/

    /*@ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}");
        if (sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomHistoryDetailInfoResponse> apiResponse = this.historyService.getRoomHistoryDetailInfo(workspaceId, sessionId);
        return ResponseEntity.ok(apiResponse);
    }*/

    /*@ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        if(userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.historyService.removeAllRoomHistory(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);
    }*/

    /*@ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest,
            BindingResult result) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", roomHistoryDeleteRequest.toString());

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.historyService.removeRoomHistory(workspaceId, roomHistoryDeleteRequest);
        return ResponseEntity.ok(apiResponse);
    }*/

    //=====
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
