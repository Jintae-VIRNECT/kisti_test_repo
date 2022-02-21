package com.virnect.download.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdminAppDeleteResponse {
	private final boolean result;
	private final LocalDateTime deletedDate;
}
