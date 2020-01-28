package com.virnect.workspace.application;

import com.virnect.workspace.global.common.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Project: PF-Workspace
 * DATE: 2020-01-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@FeignClient(name = "UserServer", url = "${user.serverUrl}")
public interface UserRestService {
    /**
     * 유저 정보 조회
     *
     * @param userId - 유저 고유 아이디
     * @return - 유저 정보
     */
@GetMapping("/{userId}")
    ResponseMessage getUserInfoByUserId(@PathVariable("userId") String userId);

/**
 * 유저 정보 리스트 검색
 *
 * @param userId - 조회 요청 유저 고유 아이디
 * @param search - 검색어
 * @return - 이름 또는 이메일이 검색어와 일치한 유저 정보들의 리스트 데이터
 */
@GetMapping
    ResponseMessage getUserInfoListUserIdAndSearchKeyword(@RequestParam("uuid") String userId, @RequestParam("search") String search, Pageable pageable);
        }
