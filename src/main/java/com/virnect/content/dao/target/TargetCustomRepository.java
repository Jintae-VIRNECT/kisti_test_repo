package com.virnect.content.dao.target;

import com.virnect.content.domain.Content;

import java.util.List;

public interface TargetCustomRepository {
    long deleteAllTargetInfoByContent(List<Content> contentList);
}
