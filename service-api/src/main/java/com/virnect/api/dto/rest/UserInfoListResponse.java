package com.virnect.api.dto.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoListResponse {
    private List<UserInfoResponse> userInfoList;
}
