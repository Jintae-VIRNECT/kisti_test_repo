package com.virnect.process.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.07.02
 */
@Getter
@RequiredArgsConstructor
public class TroubleMemoUploadResponse {
    private final boolean uploadResult;
    private final LocalDateTime uploadDate;
}
