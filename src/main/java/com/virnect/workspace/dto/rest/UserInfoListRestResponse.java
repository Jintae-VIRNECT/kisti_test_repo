package com.virnect.workspace.dto.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class UserInfoListRestResponse {
    private  List<UserInfoRestResponse> userInfoList;
    private  PageMetadataRestResponse pageMeta;
    public static List<UserInfoRestResponse> EMPTY = new ArrayList<>();
}
