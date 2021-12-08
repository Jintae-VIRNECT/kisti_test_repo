package com.virnect.data.dto.request.guest;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.virnect.data.dto.request.event.EventType;
import com.virnect.data.global.validation.CustomEnumValid;

@Getter
@Setter
public class GuestEventRequest {

	@CustomEnumValid(enumClass = EventType.class)
	private EventType event;

	@NotBlank
	private String workspaceId;

	@NotBlank
	private String userId;

}
