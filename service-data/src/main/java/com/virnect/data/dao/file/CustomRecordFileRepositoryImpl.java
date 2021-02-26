package com.virnect.data.dao.file;

import static com.virnect.data.domain.file.QRecordFile.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.data.domain.file.RecordFile;

@RequiredArgsConstructor
public class CustomRecordFileRepositoryImpl implements CustomRecordFileRepository{

	private final JPAQueryFactory query;

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
}
