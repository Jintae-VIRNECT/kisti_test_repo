package com.virnect.uaa.domain.user.dao.user;

import java.util.List;
import java.util.Optional;

import com.virnect.uaa.domain.user.domain.User;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.10
 */
public interface UserCustomRepository {
	List<User> findUserByNameAndRecoveryEmailOrInternationalNumberAndMobile(
		String name, String recoveryEmail, String mobile
	);

	Optional<User> findLoginUserInformationByUserEmail(String email);
}
