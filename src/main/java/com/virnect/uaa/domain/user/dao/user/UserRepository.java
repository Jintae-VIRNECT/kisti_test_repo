package com.virnect.uaa.domain.user.dao.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserType;

/**
 * Project: user
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Repository
 */
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
	Optional<User> findByEmail(final String email);

	Optional<User> findByUuid(final String uuid);

	Page<User> findByNameIsContainingOrEmailIsContaining(final String name, final String email, Pageable pageable);

	List<User> findByNameIsContainingOrEmailIsContaining(final String name, final String email);

	List<User> findByNicknameIsContainingOrEmailIsContaining(final String nickName, final String email);

	List<User> findByEmailIsIn(String[] email);

	boolean existsByEmail(String email);

	Page<User> findAll(Pageable pageable);

	Optional<User> findByEmailAndUuid(String email, String uuid);

	Optional<User> findByUuidAndUserType(String uuid, UserType userType);

	boolean existsByUuid(String userId);
}
