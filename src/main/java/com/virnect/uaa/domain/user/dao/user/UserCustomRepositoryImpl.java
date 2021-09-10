package com.virnect.uaa.domain.user.dao.user;

import static com.virnect.uaa.domain.user.domain.QPermission.*;
import static com.virnect.uaa.domain.user.domain.QUser.*;
import static com.virnect.uaa.domain.user.domain.QUserPermission.*;
import static com.virnect.uaa.domain.user.domain.UserType.*;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.uaa.domain.user.domain.QUser;
import com.virnect.uaa.domain.user.domain.User;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.10
 */
public class UserCustomRepositoryImpl extends QuerydslRepositorySupport implements UserCustomRepository {
	private final JPAQueryFactory query;

	public UserCustomRepositoryImpl(JPAQueryFactory Query) {
		super(User.class);
		this.query = Query;
	}

	@Override
	public List<User> findUserInformationInUUIDListWithSearchQuery(List<String> uuidList, String search) {
		return query.selectFrom(user)
			.where(
				user.uuid.in(uuidList),
				searchQuery(search)
			)
			.fetch();
	}

	@Override
	public List<User> findUserByNameAndRecoveryEmailOrInternationalNumberAndMobile(
		String name, String recoveryEmail, String mobile
	) {
		return query.selectFrom(user)
			.where(
				equalName(name), equalRecoveryEmail(recoveryEmail), equalInternationalNumber(mobile),
				equalMobile(mobile)
			)
			.fetch();
	}

	@Override
	public Page<User> findAllUserInfoWithSearchAndPagingCondition(
		String search, Pageable pageable
	) {
		QueryResults<User> pagingResult = query.selectFrom(user)
			.where(searchQuery(search))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(pagingResult.getResults(), pageable, pagingResult.getTotal());
	}

	@Override
	public List<User> findAllUserInfoWithSearchCondition(String search) {
		return query.selectFrom(user)
			.where(searchQuery(search)).fetch();
	}

	@Override
	public Optional<User> findLoginUserInformationByUserEmail(String email) {
		return Optional.ofNullable(
			query.selectFrom(user)
				.innerJoin(user.userPermissionList, userPermission).fetchJoin()
				.innerJoin(userPermission.permission, permission1).fetchJoin()
				.where(user.email.eq(email))
				.distinct().fetchOne());
	}

	@Override
	public Page<User> findAllUserBySearchAndPaging(String search, Pageable pageable) {
		JPAQuery<User> baseQuery = query.selectFrom(user)
			.where(containsSearchKeywordInEmailOrNameOrNickname(search))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		// 정렬 조건을 위해 querydsl 메서드 사용.. 추후 리팩토링
		JPQLQuery<User> pageableAndSortingQuery = getQuerydsl().applySorting(pageable.getSort(), baseQuery);
		QueryResults<User> userQueryResults = pageableAndSortingQuery.fetchResults();

		return new PageImpl<>(userQueryResults.getResults(), pageable, userQueryResults.getTotal());
	}

	@Override
	public long countCurrentSeatUserNumber(User masterUser) {
		return query.selectFrom(user).where(user.master.eq(masterUser).and(user.userType.eq(GUEST_USER))).fetchCount();
	}

	@Override
	public Optional<User> findSeatUserByMasterAndSeatUserUUID(User masterUser, String uuid) {
		User seatUser = query.selectFrom(QUser.user)
			.where(
				user.master.eq(masterUser),
				user.userType.eq(GUEST_USER),
				user.uuid.eq(uuid)
			)
			.fetchOne();
		return Optional.ofNullable(seatUser);
	}

	@Override
	public Optional<User> findWorkspaceOnlyUserByMasterAndSeatUserUUID(User masterUser, String uuid) {
		User seatUser = query.selectFrom(QUser.user)
			.where(
				user.master.eq(masterUser),
				user.userType.eq(WORKSPACE_ONLY_USER),
				user.uuid.eq(uuid)
			)
			.fetchOne();
		return Optional.ofNullable(seatUser);
	}

	@Override
	public List<User> findAllSeatUsersByWorkspaceId(String workspaceId) {
		String seatUserDomain = "@" + workspaceId + ".com";
		return query.selectFrom(user).where(user.email.endsWith(seatUserDomain)).fetch();
	}

	/**
	 *
	 *  다이나믹쿼리를 위한 조건 메서드
	 *
	 */

	private BooleanExpression containsSearchKeywordInEmailOrNameOrNickname(String search) {
		if (StringUtils.isEmpty(search)) {
			return null;
		}
		return user.email.contains(search).or(user.name.contains(search)).or(user.nickname.contains(search));
	}

	private BooleanExpression equalName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return user.name.eq(name);
	}

	private BooleanExpression equalRecoveryEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return null;
		}
		return user.recoveryEmail.eq(email);
	}

	private BooleanExpression equalInternationalNumber(String mobile) {
		if (StringUtils.isEmpty(mobile) || mobile.matches("")) {
			return null;
		}
		String internationalNumber = mobile.split("-")[0];
		return user.internationalNumber.eq(internationalNumber);
	}

	private BooleanExpression equalMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return null;
		}
		String mobileNumber = mobile.split("-")[1];
		return user.mobile.eq(mobileNumber);
	}

	private BooleanExpression searchQuery(String search) {
		if (StringUtils.isEmpty(search)) {
			return null;
		}
		return user.email.contains(search).or(user.nickname.contains(search));
	}

}
