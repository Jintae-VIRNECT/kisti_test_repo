package com.virnect.content.dao;

import com.virnect.content.domain.SceneGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.23
 */
public interface SceneGroupCustomRepository {
    Page<SceneGroup> getSceneGroupInWorkspace(String workspaceUUID, String userUUID, String search, Pageable pageable);
}
