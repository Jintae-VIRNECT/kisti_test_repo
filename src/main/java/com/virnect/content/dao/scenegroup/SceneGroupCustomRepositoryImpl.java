package com.virnect.content.dao.scenegroup;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.QSceneGroup;
import com.virnect.content.domain.SceneGroup;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

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
