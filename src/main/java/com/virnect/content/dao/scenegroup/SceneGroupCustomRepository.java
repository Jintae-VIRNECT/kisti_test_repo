package com.virnect.content.dao.scenegroup;

import java.util.List;

import com.virnect.content.domain.Content;

public interface SceneGroupCustomRepository {
	long deleteAllSceneGroupInfoByContent(List<Content> contentList);
}
