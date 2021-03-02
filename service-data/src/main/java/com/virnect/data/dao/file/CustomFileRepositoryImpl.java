package com.virnect.data.dao.file;

import static com.virnect.data.domain.file.QFile.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.data.domain.file.File;

@RequiredArgsConstructor
public class CustomFileRepositoryImpl implements CustomFileRepository {

	private final JPAQueryFactory query;

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
}
