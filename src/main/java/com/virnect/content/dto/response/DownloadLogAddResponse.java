package com.virnect.content.dto.response;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-02-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public class DownloadLogAddResponse {
    private final boolean downloadLogAddResult;
}
