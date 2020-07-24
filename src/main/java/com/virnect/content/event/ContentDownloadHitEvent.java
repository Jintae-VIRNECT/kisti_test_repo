package com.virnect.content.event;

import com.virnect.content.domain.Content;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.06
 */
@Getter
@RequiredArgsConstructor
public class ContentDownloadHitEvent {
    private final Content content;
}
