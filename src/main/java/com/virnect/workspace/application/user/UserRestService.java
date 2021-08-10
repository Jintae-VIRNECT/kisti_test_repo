package com.virnect.workspace.application.user;

import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@FeignClient(name = "account-server", fallbackFactory = UserRestFallbackFactory.class, configuration = FeignConfiguration.class)
public interface UserRestService {
    /**
     * 유저 정보 조회
     *
     * @param userId - 유저 고유 아이디
     * @return - 유저 정보
     */
    @GetMapping("/users/{userId}")
    ApiResponse<UserInfoRestResponse> getUserInfoByUserId(@PathVariable("userId") String userId);

    /**
     * 유저 정보 리스트 검색
     *
     * @param userId - 조회 요청 유저 고유 아이디
     * @param search - 검색어
     * @return - 이름 또는 이메일이 검색어와 일치한 유저 정보들의 리스트 데이터
     */
    @GetMapping("/users")
    ApiResponse<UserInfoListRestResponse> getUserInfoListUserIdAndSearchKeyword(
            @RequestParam("uuid") String userId, @RequestParam("search") String search,
            @RequestParam("paging") boolean paging, Pageable pageable
    );

    /**
     * 유저 중복 여부 조회
     *
     * @param emailList - 조회 요청 유저 이메일 리스트
     * @return - 유저 정보
     */
    @GetMapping("/users/invite")
    ApiResponse<InviteUserInfoResponse> getInviteUserInfoByEmail(@RequestParam("email") String email);

    @PostMapping("/users/list")
    ApiResponse<UserInfoListRestResponse> getUserInfoList(
            @RequestParam("search") String search, @RequestBody List<String> workspaceUserIdList
    );

    //멤버 등록
    @PostMapping("/users/register/member")
    ApiResponse<UserInfoRestResponse> registerMemberRequest(
            @RequestBody RegisterMemberRequest registerMemberRequest, @RequestHeader("serviceID") String serviceID
    );

    //멤버 삭제
    @DeleteMapping("/users/{userUUID}")
    ApiResponse<UserDeleteRestResponse> userDeleteRequest(
            @PathVariable("userUUID") String userUUId, @RequestHeader("serviceID") String serviceID
    );

    //개인정보 접근 인증
    @PostMapping("/users/{userId}/access")
    ApiResponse<UserInfoAccessCheckResponse> userInfoAccessCheckRequest(
            @PathVariable("userId") String userId, @RequestBody UserInfoAccessCheckRequest userInfoAccessCheckRequest
    );

    //멤버 비밀번호 변경
    @PostMapping("/users/member/password")
    ApiResponse<MemberUserPasswordChangeResponse> memberUserPasswordChangeRequest(
            @RequestHeader("serviceID") String serviceID,
            @RequestBody MemberUserPasswordChangeRequest memberUserPasswordChangeRequest
    );

    //개인 정보 수정
    @PostMapping("/users/{userId}")
    ApiResponse<UserInfoRestResponse> modifyUserInfoRequest(@PathVariable("userId") String userId, @RequestBody UserInfoModifyRequest userInfoModifyRequest);

    //프로필 변경
    @PostMapping("/users/{userId}/profile")
    ApiResponse<UserProfileUpdateResponse> modifyUserProfileRequest(@PathVariable("userId") String userId, @ModelAttribute MultipartFile profile);
}

