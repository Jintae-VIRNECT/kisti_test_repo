package com.virnect.data.redis.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusMessage implements Serializable {

	private static final long serialVersionUID = 2082503192322391880L;
	private String uuid;
	private String name;
	private String email;
	private String status;

	@Override
	public String toString() {
		return "StatusMessage{" +
			"uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", email='" + email + '\'' +
			", status='" + status + '\'' +
			'}';
	}
}