package com.virnect.data.dto.request.guest;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.virnect.data.global.validation.CustomEnumValid;

@Getter
@Setter
public class GuestEventRequest {

	@CustomEnumValid(enumClass = GuestEvent.class)
	private GuestEvent event;

	@NotBlank
	private String workspaceId;

	@NotBlank
	private String userId;

}
