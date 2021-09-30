package com.virnect.data.dao.file;

import static com.virnect.data.domain.file.QRecordFile.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.file.RecordFile;

@Repository
public class CustomRecordFileRepositoryImpl extends QuerydslRepositorySupport implements CustomRecordFileRepository{

	private final JPAQueryFactory query;

	public CustomRecordFileRepositoryImpl(JPAQueryFactory query) {
		super(RecordFile.class);
		this.query = query;
	}

	@Override
	public List<RecordFile> findByWorkspaceIdAndSessionIdAndDeleted(
		String workspaceId, String sessionId, boolean delete
	) {
		return query.selectFrom(recordFile)
			.where(
				recordFile.workspaceId.eq(workspaceId),
				recordFile.sessionId.eq(sessionId),
				recordFile.deleted.eq(delete)
			).fetch();
	}

	@Override
	public List<RecordFile> findByWorkspaceIdAndDeleted(
		String workspaceId,
		boolean deleted
	) {
		return query.selectFrom(recordFile)
			.where(
				recordFile.workspaceId.eq(workspaceId),
				recordFile.deleted.eq(deleted)
			).fetch();
	}

	@Override
	public Page<RecordFile> findByWorkspaceIdAndSessionIdAndDeletedUsePaging(
		String workspaceId,
		String sessionId,
		boolean deleted,
		Pageable pageable
	) {
		JPQLQuery<RecordFile> queryResult = query
			.selectFrom(recordFile)
			.where(
				recordFile.workspaceId.eq(workspaceId),
				recordFile.sessionId.eq(sessionId),
				recordFile.deleted.eq(deleted)
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<RecordFile> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		return new PageImpl<>(results, pageable, totalCount);
	}
}
