package com.virnect.serviceserver.gateway.api;

import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.request.*;
import com.virnect.serviceserver.gateway.dto.response.*;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class RoomRestController {
    private static final String TAG = "RoomRestController";

    private final RemoteGatewayService remoteGatewayService;
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    /*@ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회합니다.")
    @GetMapping(value = "room")
    public ResponseEntity<?> getRoomList(
            @RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats
    ) {
        log.debug("getRoomList");
        ApiResponse<SessionListResponse> apiResponse = this.remoteGatewayService.getSessionList(webRtcStats);
        log.info("getSessionList {}", apiResponse.toString());

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
    }*/

    @ApiOperation(value = "Initialize a Remote Room ", notes = "원격협업 방을 생성합니다.")
    @ApiImplicitParams({
    })
    @PostMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            @RequestBody @Valid RoomRequest roomRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result) {

        log.info(TAG, "createRoomRequestHandler");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomResponse> roomResponse = this.remoteGatewayService.createRoom(roomRequest, roomProfileUpdateRequest);
        return ResponseEntity.ok(roomResponse);
    }

    @ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "검색어(협업, 멤버 이름 검색)", dataType = "string", allowEmptyValue = true, defaultValue = "remote"),
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam("paging") boolean paging,
            @RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats,
            @ApiIgnore PageRequest pageable
    ) {
        log.debug("getRoomList");
        ApiResponse<RoomInfoListResponse> apiResponse = this.remoteGatewayService.getRoomInfoList(search, paging, pageable.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Load Room Detail Information", notes = "특정 원격협업 방 상제 정보를 조회하는 API 입니다.")
    @GetMapping(value = "room/{sessionId}")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(@PathVariable("sessionId") String sessionId) {
        log.info(TAG, "getRoomById");
        if (sessionId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomDetailInfoResponse> apiResponse = this.remoteGatewayService.getRoomInfoBySessionId(sessionId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete Specific Room", notes = "특정 원격협업 방을 삭제하는 API 입니다.")
    @DeleteMapping(value = "room/{sessionId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRoomById(@PathVariable("sessionId") String sessionId) {
        log.info(TAG, "deleteRoomById");
        if(sessionId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.removeRoom(sessionId);
        return ResponseEntity.ok(apiResponse);

    }

    @ApiOperation(value = "Update Room Information", notes = "특정 원격협업 방 상세 정보를 수정하는 API 입니다.")
    @PostMapping(value = "room/{sessionId}/info")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomById(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result
    ) {
        log.info(TAG, "updateRoomById");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomDetailInfoResponse> apiResponse = this.remoteGatewayService.modifyRoomInfo(sessionId, modifyRoomInfoRequest, roomProfileUpdateRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Leave Specific Room", notes = "특정 원격협업 방을 나가는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "원격협업 방 Session ID", dataType = "string", defaultValue = "", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "사용자 uuid", dataType = "string", defaultValue = "473b12854daa6afeb9e505551d1b2743", paramType = "query", required = true),
    })
    @DeleteMapping(value = "room/{sessionId}/leave")
    public ResponseEntity<ApiResponse<Boolean>> leaveRoomById(
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId
    ) {
        log.info(TAG, "leaveRoomById");
        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.leaveRoom(sessionId, userId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Invite a Member to Specific Room", notes = "특정 멤버를 원격협업 방에 초대하는 API 입니다.")
    @PostMapping(value = "room/{sessionId}/member")
    public ResponseEntity<ApiResponse<Boolean>> inviteMember(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result
    ) {
        log.info(TAG, "inviteMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.inviteRoom(sessionId, inviteRoomRequest);

        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Kick out a specific member from a specific room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @DeleteMapping(value = "room/{sessionId}/member")
    public ResponseEntity<ApiResponse<Boolean>> kickOutMember(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid KickRoomRequest kickRoomRequest,
            BindingResult result
    ) {
        log.info(TAG, "kickOutMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.kickFromRoom(sessionId, kickRoomRequest);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 특정 사용자 삭제 원격협업 방에서 삭제
     * 특정 사용자 원격협업 방에 추가
     * 특정 사용자
     */
    /*@ApiOperation(value = "Delete Specific Member from Room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @GetMapping(value = "room/{sessionId}/participants")
    public ResponseEntity<?> getParticipantsByRoomId(@RequestParam("name") String name,
                                            @RequestParam("page") int page) {
        log.info(TAG, "getHistoryByRoomId");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
    }*/
}
