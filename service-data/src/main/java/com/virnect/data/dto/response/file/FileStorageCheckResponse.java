package com.virnect.data.dto.response.file;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.virnect.data.error.ErrorCode;

@Getter
@Setter
@Builder
public class FileStorageCheckResponse {

	private ErrorCode errorCode;
	private int usedStoragePer;
}
