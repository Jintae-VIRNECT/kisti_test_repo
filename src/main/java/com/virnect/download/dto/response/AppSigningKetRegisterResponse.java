package com.virnect.download.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AppSigningKetRegisterResponse {
	private final List<SignedAppInfoResponse> signedAppInfoList;
}
