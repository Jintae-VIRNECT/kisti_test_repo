package com.virnect.download.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Project: PF-Download
 * DATE: 2020-05-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Setter
@Getter
public class AppResponse {
    private LocalDateTime releaseTime;
    private String version;
    private String productName;
    private Long downloadCount;
}
