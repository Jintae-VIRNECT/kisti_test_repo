package com.virnect.download.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AppSigningKetRegisterResponse {
    private final List<SignedAppInfoResponse> signedAppInfoList;
}
