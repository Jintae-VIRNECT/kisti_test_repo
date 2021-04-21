package com.virnect.uaa.domain.auth.domain.user;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Jwt Token Domain Data Class
 * @since 2020.03.17
 */
@Getter
@RedisHash("Token")
public class Token {
	@Id
	private String accessToken;
	private String email;
	private String name;
	private String uuid;
	private String os;
	private String device;
	private String ip;
	private String lastUsedDate;
	private LocalDateTime createdDate;

}
