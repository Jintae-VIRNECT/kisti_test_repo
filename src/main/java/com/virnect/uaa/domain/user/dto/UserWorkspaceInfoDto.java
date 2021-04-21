package com.virnect.uaa.domain.user.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: user
 * DATE: 2020-01-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class UserWorkspaceInfoDto {
	private String uuid;
	private String pinNumber;
	private String description;
	private String role;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;

	@Builder
	public UserWorkspaceInfoDto(
		final String uuid, final String pinNumber, final String name, final String description, final String role,
		final LocalDateTime createdDate, final LocalDateTime updatedDate
	) {
		this.uuid = uuid;
		this.pinNumber = pinNumber;
		this.description = description;
		this.role = role;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}
}
