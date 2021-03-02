package com.virnect.data.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoListResponse {
    private List<UserInfoResponse> userInfoList;
}
