package com.virnect.content.dao.target;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.QTarget;
import com.virnect.content.domain.Target;

public class TargetCustomRepositoryImpl extends QuerydslRepositorySupport implements TargetCustomRepository {

	public TargetCustomRepositoryImpl() {
		super(Target.class);
	}

	@Override
	public long deleteAllTargetInfoByContent(List<Content> contentList) {
		QTarget qTarget = QTarget.target;
		return delete(qTarget).where(qTarget.content.in(contentList)).execute();
	}
}
