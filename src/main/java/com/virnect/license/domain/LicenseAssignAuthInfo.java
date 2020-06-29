package com.virnect.license.domain;

import com.virnect.license.dto.response.biling.ProductInfoResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RedisHash("LicenseAssignAuthInfo")
public class LicenseAssignAuthInfo {
    @Id
    private String assignAuthCode;
    private Long userId;
    private String uuid;
    private String userName;
    private String email;
    private List<ProductInfoResponse> productInfoList;
    private LocalDateTime assignableCheckDate;
    @TimeToLive
    private LocalDateTime expiredDate;
}
