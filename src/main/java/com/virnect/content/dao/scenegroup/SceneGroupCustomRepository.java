package com.virnect.content.dao.scenegroup;

import com.virnect.content.domain.Content;

import java.util.List;

public interface SceneGroupCustomRepository {
    long deleteAllSceneGroupInfoByContent(List<Content> contentList);
}
