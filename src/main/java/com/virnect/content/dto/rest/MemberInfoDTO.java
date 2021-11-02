package com.virnect.content.dto.rest;

import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.rest.Role;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.06.03
 */
@Getter
@Setter
public class MemberInfoDTO {
	private String uuid;
	private String name;
	private String nickName;
	private String profile;
	private Role role;

	@Override
	public String toString() {
		return "MemberInfoDTO{" +
			"uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", nickName='" + nickName + '\'' +
			", profile='" + profile + '\'' +
			", role=" + role +
			'}';
	}
}
