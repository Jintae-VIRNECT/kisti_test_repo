package com.virnect.content.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.content.domain.QContent;
import com.virnect.content.domain.QSceneGroup;
import com.virnect.content.domain.SceneGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.23
 */
public class SceneGroupCustomRepositoryImpl extends QuerydslRepositorySupport implements SceneGroupCustomRepository {
    public SceneGroupCustomRepositoryImpl() {
        super(SceneGroup.class);
    }

    @Override
    public Page<SceneGroup> getSceneGroupInWorkspace(String workspaceUUID, String userUUID, String search, Pageable pageable) {
        QSceneGroup qSceneGroup = QSceneGroup.sceneGroup;
        QContent qContent = QContent.content;
        JPQLQuery<SceneGroup> query = from(qSceneGroup);

        if (search != null) {
           query = query.where(qSceneGroup.name.contains(search));
        }

        if (workspaceUUID != null) {
            query = query.join(qSceneGroup.content, qContent).where(qContent.workspaceUUID.eq(workspaceUUID));
        }

        long totalCount = query.fetchCount();

        List<SceneGroup> sceneGroupList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(sceneGroupList, pageable, totalCount);
    }
}
