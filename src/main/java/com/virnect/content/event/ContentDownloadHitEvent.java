package com.virnect.content.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.content.domain.Content;

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
	private final String downloader;
}
