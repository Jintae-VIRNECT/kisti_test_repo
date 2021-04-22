package com.virnect.uaa.domain.auth.account.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;
import lombok.Setter;

import com.virnect.uaa.global.security.token.JwtPayload;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Block Token Information
 * @since 2020.03.20
 */

@Getter
@Setter
@RedisHash("BlockToken")
public class BlockToken {
	@Id
	private String tokenId;
	private String email;
	private String name;
	private JwtPayload tokenPayload;
	private BlockReason blockReason;
	private String blockedBy;
	private LocalDateTime blockStartDate;
	@TimeToLive
	private Long blockExpire;

	@Override
	public String toString() {
		return "BlockToken{" +
			"tokenId='" + tokenId + '\'' +
			", email='" + email + '\'' +
			", name='" + name + '\'' +
			", blockReason=" + blockReason +
			", blockedBy='" + blockedBy + '\'' +
			", blockStartDate=" + blockStartDate +
			", blockExpire=" + blockExpire +
			'}';
	}
}
