package com.virnect.data.dao.file;

import static com.virnect.data.domain.file.QFile.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.domain.member.MemberHistory;

@Repository
public class CustomFileRepositoryImpl extends QuerydslRepositorySupport implements CustomFileRepository {

	private final JPAQueryFactory query;
	private final long EXPIRATION_DATE = 30;

	public CustomFileRepositoryImpl(JPAQueryFactory query) {
		super(File.class);
		this.query = query;
	}

	@Override
	public List<File> findByWorkspaceIdAndSessionIdAndDeleted(
		String workspaceId,
		String sessionId,
		boolean delete
	) {
		return query.selectFrom(file)
			.where(
				file.workspaceId.eq(workspaceId),
				file.sessionId.eq(sessionId),
				file.deleted.eq(delete)
			).fetch();
	}

	@Override
	public List<File> findByWorkspaceIdAndDeleted(
		String workspaceId,
		boolean delete
	) {
		return query.selectFrom(file)
			.where(
				file.workspaceId.eq(workspaceId),
				file.deleted.eq(delete)
			).fetch();
	}

	@Override
	public Page<File> findShareFileByWorkspaceAndSessionId(
		String workspaceId,
		String sessionId,
		boolean paging,
		Pageable pageable
	) {
		JPQLQuery<File> queryResult = query
			.selectFrom(file)
			.where(
				file.workspaceId.eq(workspaceId),
				file.sessionId.eq(sessionId),
				file.fileType.eq(FileType.SHARE),
				file.objectName.contains("thumbnail").not(),
				file.deleted.eq(false)
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<File> results;
		if (paging) {
			results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		} else {
			results = Objects.requireNonNull(queryResult.fetch());
		}
		return new PageImpl<>(results, pageable, totalCount);
	}

	@Override
	public Page<File> findByWorkspaceIdAndSessionIdAndDeletedAndFileType(
		String workspaceId,
		String sessionId,
		boolean deleted,
		Pageable pageable
	) {
		JPQLQuery<File> queryResult = query
			.selectFrom(file)
			.where(
				file.workspaceId.eq(workspaceId),
				file.sessionId.eq(sessionId),
				file.deleted.eq(deleted),
				file.objectName.contains("thumbnail").not(),
				file.fileType.ne(FileType.RECORD),
				file.fileType.ne(FileType.PROFILE)
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<File> results;
		if (pageable.isPaged()) {
			results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		} else {
			results = Objects.requireNonNull(queryResult.fetch());
		}
		return new PageImpl<>(results, pageable, totalCount);
	}
}