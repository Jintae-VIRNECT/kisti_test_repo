package com.virnect.service.api;

import com.virnect.service.ApiResponse;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.ResultResponse;
import com.virnect.service.dto.service.request.RoomHistoryDeleteRequest;
import com.virnect.service.dto.service.request.RoomRequest;
import com.virnect.service.dto.service.response.RoomHistoryDetailInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoListResponse;
import com.virnect.service.dto.service.response.RoomResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RequestMapping("/remote")
public interface IHistoryRestAPI {

    @ApiOperation(value = "Redial a History Remote Room with Company Code", notes = "This api will be deprecated")
    @PostMapping(value = "history")
    ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequest(
            @RequestBody @Valid RoomRequest roomRequest,
            @RequestParam(name = "sessionId") String sessionId,
            @RequestParam(name = "companyCode") int companyCode,
            BindingResult result
            );

    @ApiOperation(value = "Redial a History Remote Room with Company Code", notes = "Redial Remote Session")
    @PostMapping(value = "history/{userId}")
    ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequestHandler(
            @RequestHeader(name = "client", required = false) String client,
            @PathVariable(name = "userId") String userId,
            @RequestBody @Valid RoomRequest roomRequest,
            @RequestParam(name = "sessionId") String sessionId,
            @RequestParam(name = "companyCode") int companyCode,
            BindingResult result);

    @ApiOperation(value = "Load Room History Information List", notes = "최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "history")
    ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "paging") boolean paging,
            @ApiIgnore PageRequest pageable
    );

    @ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{workspaceId}/{sessionId}")
    ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId);

    @ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{userId}")
    ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("userId") String userId);

    @ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}")
    ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest,
            BindingResult result);

}
