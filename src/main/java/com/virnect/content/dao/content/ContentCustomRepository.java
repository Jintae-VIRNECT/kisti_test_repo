package com.virnect.content.dao.content;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.Tuple;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.TargetType;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description Content Domain QueryDsl Interface
 * @since 2020.03.13
 */
public interface ContentCustomRepository {
	Page<Content> getContent(
		String workspaceUUID, String userUUID, String search, String shareds, String converteds,
		List<String> userUUIDList, Pageable pageable, List<String> targetType
	);

    Optional<Content> getContentOfTarget(String targetData);

	Long getWorkspaceStorageSize(String workspaceUUID);

	List<Tuple> countByUsers(String workspaceUUID, List<String> userUUIDList);

	Long calculateTotalStorageAmountByWorkspaceId(String workspaceId);

	long deleteAllContentByWorkspaceUUID(String workspaceUUID);

	List<Content> findContentAndTargetListByWorkspaceUUID(String workspaceUUID);
}
