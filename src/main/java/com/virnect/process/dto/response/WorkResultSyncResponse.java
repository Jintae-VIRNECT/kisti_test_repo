package com.virnect.process.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.03.04
 */
@Getter
@RequiredArgsConstructor
public class WorkResultSyncResponse {
    private final boolean syncResult;
    private final LocalDateTime syncedDate;
}
