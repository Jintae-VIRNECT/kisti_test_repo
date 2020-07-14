package com.virnect.download.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-Download
 * DATE: 2020-05-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class AppInfoListResponse {
    private final List<AppInfo> appInfoList;
    @Getter
    @Setter
    public static class AppInfo{
        private Long id;
        private String uuid;
        private LocalDateTime releaseTime;
        private String version;
        private Long appDownloadCount;
        private Long guideDownloadCount;
        private String os;
        private String device;
        private String appUrl;
        private String guideUrl;
        private String imageUrl;
    }
}
