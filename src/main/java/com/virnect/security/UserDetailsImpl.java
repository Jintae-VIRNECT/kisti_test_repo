package com.virnect.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize
@JsonDeserialize
@EqualsAndHashCode(of = {"userId", "uuid", "email"}, callSuper = false)
public class UserDetailsImpl{
	private long userId;
	private String uuid;
	private String email;
	private String name;
	private String nickname;
	private String userType;
	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	@Override
	public String toString() {
		return "UserDetailsImpl{" +
			"userId=" + userId +
			", uuid='" + uuid + '\'' +
			", email='" + email + '\'' +
			", name='" + name + '\'' +
			", nickname='" + nickname + '\'' +
			", userType='" + userType + '\'' +
			", username='" + username + '\'' +
			", password='" + password + '\'' +
			", authorities=" + authorities +
			", accountNonExpired=" + accountNonExpired +
			", accountNonLocked=" + accountNonLocked +
			", credentialsNonExpired=" + credentialsNonExpired +
			", enabled=" + enabled +
			'}';
	}
}
