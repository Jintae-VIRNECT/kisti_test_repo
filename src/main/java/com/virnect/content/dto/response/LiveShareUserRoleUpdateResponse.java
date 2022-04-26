package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LiveShareUserRoleUpdateResponse {
	private final boolean result;
	private final LocalDateTime updateDate;
}
