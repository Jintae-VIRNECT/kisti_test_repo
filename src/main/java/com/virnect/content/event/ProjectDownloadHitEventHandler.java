package com.virnect.content.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.contentdonwloadlog.ContentDownloadLogRepository;
import com.virnect.content.dao.project.ProjectDownloadLogRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.ContentDownloadLog;
import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectDownloadLog;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-10
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class ProjectDownloadHitEventHandler {
	private final ProjectDownloadLogRepository projectDownloadLogRepository;

	@EventListener
	public void ProjectDownloadHit(ProjectDownloadHitEvent event) {
		Project project = event.getProject();
		ProjectDownloadLog projectDownloadLog = ProjectDownloadLog.builder()
			.workspaceUUID(project.getWorkspaceUUID())
			.projectName(project.getName())
			.projectSize(project.getSize())
			.uploadUserUUID(project.getUserUUID())
			.downloadUserUUID(event.getDownloadUserUUID())
			.build();
		projectDownloadLogRepository.save(projectDownloadLog);
		log.info(
			"[PROJECT DOWNLOAD HIT EVENT] workspaceUUID : {}, projectName : {}, projectSize : {}, uploaderUUID : {}, downloaderUUID : {}",
			project.getWorkspaceUUID(), project.getName(), project.getSize(), project.getUserUUID(),
			event.getDownloadUserUUID()
		);
	}

}