package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.RoomDetailInfoResponse;
import com.virnect.data.dto.response.RoomInfoListResponse;
import com.virnect.data.dto.response.RoomResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RequestMapping("/remote")
public interface ISessionRestAPI {

    /**
     * 1. check room request handler
     * 2. check user license type using user uuid
     * 3. generate session id and token
     * 4. create room
     * 5. register user as a leader who creates the room
     * 6. register other users as a worker(participant), if the request contains other user information.
     * 7. return session id and token
     */
    @ApiOperation(value = "Initialize a Remote Room ", notes = "원격협업 방을 생성 합니다.")
    @ApiImplicitParams({
    })
    @PostMapping(value = "room")
    ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            @RequestBody @Valid RoomRequest roomRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result);

    @ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "room")
    ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam("paging") boolean paging,
            @ApiIgnore PageRequest pageRequest
    );

    @ApiOperation(value = "Load Room Detail Information", notes = "특정 원격협업 방 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "room/{workspaceId}/{sessionId}")
    ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId);

    @ApiOperation(value = "Delete Specific Room", notes = "특정 원격협업 방을 삭제하는 API 입니다.")
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/{userId}")
    ResponseEntity<ApiResponse<ResultResponse>> deleteRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId);

    @ApiOperation(value = "Update Room Information", notes = "특정 원격협업 방 상세 정보를 수정하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/info")
    ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result
    );

    @ApiOperation(value = "Join a Specific Room", notes = "특정 원격협업 방에 접속하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/join")
    ResponseEntity<ApiResponse<RoomResponse>> joinRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid JoinRoomRequest joinRoomRequest,
            BindingResult result
    );

    @ApiOperation(value = "Exit Specific Room", notes = "특정 원격협업 방을 나가는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "원격협업 방 Session ID", dataType = "string", defaultValue = "", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "사용자 uuid", dataType = "string", defaultValue = "", paramType = "query", required = true),
    })
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/exit")
    ResponseEntity<ApiResponse<ResultResponse>> exitRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId
    );

    /*@ApiOperation(value = "Invite a Member to Specific Room", notes = "특정 멤버를 원격협업 방에 초대하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/member")
    public ResponseEntity<ApiResponse<InviteRoomResponse>> inviteMember(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result
    ) {
        log.info(TAG, "inviteMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<InviteRoomResponse> apiResponse = this.sessionService.inviteRoom(workspaceId, sessionId, inviteRoomRequest);
        return ResponseEntity.ok(apiResponse);
    }*/

    @ApiOperation(value = "Kick out a specific member from a specific room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/member")
    ResponseEntity<ApiResponse<ResultResponse>> kickOutMember(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid KickRoomRequest kickRoomRequest,
            BindingResult result
    );
}
