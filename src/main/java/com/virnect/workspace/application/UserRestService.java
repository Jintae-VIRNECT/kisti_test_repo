package com.virnect.workspace.application;

import com.virnect.workspace.dto.rest.InviteUserInfoRestResponse;
import com.virnect.workspace.dto.rest.UserInfoListRestResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


/**
 * Project: PF-Workspace
 * DATE: 2020-01-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@FeignClient(name = "user-server")
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
    ApiResponse<UserInfoListRestResponse> getUserInfoListUserIdAndSearchKeyword(@RequestParam("uuid") String userId, @RequestParam("search") String search, @RequestParam("paging") boolean paging, Pageable pageable);

    /**
     * 유저 중복 여부 조회
     *
     * @param emailList - 조회 요청 유저 이메일 리스트
     * @return - 유저 정보
     */
    @GetMapping("/users/invite")
    ApiResponse<InviteUserInfoRestResponse> getUserInfoByEmailList(@RequestParam("email[]") String[] emailList);

    @PostMapping("/users/list")
    ApiResponse<UserInfoListRestResponse> getUserInfoList(@RequestParam("search") String search, @RequestBody String[] workspaceUserIdList);
}

