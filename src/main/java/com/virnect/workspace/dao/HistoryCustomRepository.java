package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Workspace;

public interface HistoryCustomRepository {
    long deleteAllHistoryInfoByWorkspace(Workspace workspace);
}
