package com.virnect.content.dto.request;

import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.Role;

@Setter
@Getter
public class LiveShareUserUpdateRequest {
	private Role role;
}
