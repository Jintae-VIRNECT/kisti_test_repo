package com.virnect.content.dao;

import com.virnect.content.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description Content Domain QueryDsl Interface
 * @since 2020.03.13
 */
public interface ContentCustomRepository {
    Page<Content> getContent(String workspaceUUID, String search, String shareds, List<String> userUUIDList, Pageable pageable);
}
