package com.virnect.uaa.domain.user.dao.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserType;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.10
 */
public interface UserCustomRepository {
	List<User> findUserInformationInUUIDListWithSearchQuery(List<String> uuidList, String search);

	List<User> findUserByNameAndRecoveryEmailOrInternationalNumberAndMobile(
		String name, String recoveryEmail, String mobile
	);

	Page<User> findAllUserInfoWithSearchAndPagingCondition(String search, Pageable pageable);

	List<User> findAllUserInfoWithSearchCondition(String search);

	Optional<User> findLoginUserInformationByUserEmail(String email);

	Page<User> findAllUserBySearchAndPaging(String search, Pageable pageable);

	long countCurrentSeatUserNumber(User masterUser);

	Optional<User> findSeatUserByMasterAndSeatUserUUID(User master, String uuid);
}
