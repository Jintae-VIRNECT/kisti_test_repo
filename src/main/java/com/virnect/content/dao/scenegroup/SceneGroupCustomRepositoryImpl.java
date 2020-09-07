package com.virnect.content.dao.scenegroup;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.QSceneGroup;
import com.virnect.content.domain.SceneGroup;

public class SceneGroupCustomRepositoryImpl extends QuerydslRepositorySupport implements SceneGroupCustomRepository {

	public SceneGroupCustomRepositoryImpl() {
		super(SceneGroup.class);
	}

	@Override
	public long deleteAllSceneGroupInfoByContent(List<Content> contentList) {
		QSceneGroup qSceneGroup = QSceneGroup.sceneGroup;
		return delete(qSceneGroup).where(qSceneGroup.content.in(contentList)).execute();
	}
}
