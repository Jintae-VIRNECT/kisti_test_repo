package com.virnect.content.dao.target;

import java.util.List;

import com.virnect.content.domain.Content;

public interface TargetCustomRepository {
	long deleteAllTargetInfoByContent(List<Content> contentList);
}
