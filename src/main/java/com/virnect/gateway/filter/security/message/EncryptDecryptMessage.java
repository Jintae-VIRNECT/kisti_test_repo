package com.virnect.gateway.filter.security.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptDecryptMessage {
	private String data;

	@Override
	public String toString() {
		return "EncryptDecryptMessage{" +
			"data='" + data + '\'' +
			'}';
	}
}
