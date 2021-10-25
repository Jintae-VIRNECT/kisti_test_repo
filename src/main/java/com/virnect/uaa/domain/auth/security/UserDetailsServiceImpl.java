package com.virnect.uaa.domain.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.security.UserDetailsImpl;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("LOAD_USER_NAME: [{}]", email);
		User user = userRepository.findLoginUserInformationByUserEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException(email)
			);
		return new UserDetailsImpl(user);
	}
}