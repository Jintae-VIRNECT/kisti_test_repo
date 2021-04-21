package com.virnect.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import com.virnect.uaa.domain.user.domain.User;

@Getter
@JsonSerialize
@EqualsAndHashCode(of = {"userId", "uuid", "email"}, callSuper = false)
public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {
	private final long userId;
	private final String uuid;
	private final String email;
	private final String name;
	private final String nickname;
	private final String userType;

	public UserDetailsImpl(User user) {
		super(
			user.getEmail(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(),
			user.isCredentialsNonExpired(), user.isAccountNonLocked(),
			authorities(user)
		);
		this.userId = user.getId();
		this.uuid = user.getUuid();
		this.email = user.getEmail();
		this.name = user.getName();
		this.nickname = user.getNickname();
		this.userType = user.getUserType().name();
	}

	private static Collection<? extends GrantedAuthority> authorities(User user) {
		return user.getUserPermissionList()
			.stream()
			.map(p -> p.getPermission().getPermission())
			.map(SimpleGrantedAuthority::new)
			.collect(
				Collectors.toSet());
	}

	@Override
	public String toString() {
		return "UserDetailsImpl{" +
			"userId=" + userId +
			", uuid='" + uuid + '\'' +
			", email='" + email + '\'' +
			", name='" + name + '\'' +
			", userType=" + userType +
			'}';
	}
}
