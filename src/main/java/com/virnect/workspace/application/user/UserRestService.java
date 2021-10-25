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
    //유저 정보 조회
    @GetMapping("/users/{userId}")
    ApiResponse<UserInfoRestResponse> getUserInfoByUserId(@PathVariable("userId") String userId);

    //유저 정보 리스트 검색
    @GetMapping("/users")
    ApiResponse<UserInfoListRestResponse> getUserInfoListUserIdAndSearchKeyword(
            @RequestParam("uuid") String userId, @RequestParam("search") String search,
            @RequestParam("paging") boolean paging, Pageable pageable
    );

    //유저 중복 여부 조회
    @GetMapping("/users/invite")
    ApiResponse<InviteUserInfoResponse> getInviteUserInfoByEmail(@RequestParam("email") String email);

    //사용자 정보 목록 조회
    @PostMapping("/users/list")
    ApiResponse<UserInfoListRestResponse> getUserInfoList(
            @RequestParam("search") String search, @RequestBody List<String> workspaceUserIdList
    );

    //멤버 등록
    @PostMapping("/users/members")
    ApiResponse<UserInfoRestResponse> registerMemberRequest(
            @RequestBody MemberRegistrationRequest memberRegistrationRequest, @RequestHeader("serviceID") String serviceID
    );

    //멤버 삭제
    @DeleteMapping("/users/members")
    ApiResponse<UserDeleteRestResponse> userDeleteRequest(
            @RequestBody MemberDeleteRequest memberDeleteRequest, @RequestHeader("serviceID") String serviceID
    );

    //개인정보 접근 인증
    @PostMapping("/users/{userId}/access")
    ApiResponse<UserInfoAccessCheckResponse> userInfoAccessCheckRequest(
            @PathVariable("userId") String userId, @RequestBody UserInfoAccessCheckRequest userInfoAccessCheckRequest
    );

    //멤버 비밀번호 변경
    @PostMapping("/users/members/password")
    ApiResponse<MemberUserPasswordChangeResponse> memberUserPasswordChangeRequest(
            @RequestBody MemberUserPasswordChangeRequest memberUserPasswordChangeRequest, @RequestHeader("serviceID") String serviceID
    );

    //개인 정보 수정
    @PostMapping("/users/{userId}")
    ApiResponse<UserInfoRestResponse> modifyUserInfoRequest(@PathVariable("userId") String userId, @RequestBody UserInfoModifyRequest userInfoModifyRequest);

    //프로필 변경
    @RequestMapping(method = RequestMethod.POST, value = "/users/{userId}/profile", consumes = "multipart/form-data")
    ApiResponse<UserProfileUpdateResponse> modifyUserProfileRequest(@PathVariable("userId") String userId, @RequestPart("profile") MultipartFile profile, @RequestParam("updateAsDefaultImage") Boolean updateAsDefaultImage);

    //워크스페이스 시트 계정 등록
    @PostMapping("/users/members/guest")
    ApiResponse<UserInfoRestResponse> guestMemberRegistrationRequest(@RequestBody GuestMemberRegistrationRequest guestMemberRegistrationRequest, @RequestHeader("serviceID") String serviceID);

    //워크스페이스 시트 계정 삭제
    @DeleteMapping("/users/members/guest")
    ApiResponse<UserDeleteRestResponse> guestMemberDeleteRequest(@RequestBody GuestMemberDeleteRequest guestMemberDeleteRequest, @RequestHeader("serviceID") String serviceID);
}

