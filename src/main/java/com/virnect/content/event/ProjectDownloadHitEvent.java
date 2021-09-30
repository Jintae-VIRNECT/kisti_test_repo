package com.virnect.content.event;

import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.ContentDownloadLog;
import com.virnect.content.domain.project.Project;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-10
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class ProjectDownloadHitEvent {
	private final Project project;
	private final String downloadUserUUID;
}


