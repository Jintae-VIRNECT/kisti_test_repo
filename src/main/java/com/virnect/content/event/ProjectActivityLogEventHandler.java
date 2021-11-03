package com.virnect.content.event;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.project.ProjectActivityLogRepository;
import com.virnect.content.domain.project.ProjectActivityLog;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProjectActivityLogEventHandler {
	private final ProjectActivityLogRepository projectActivityLogRepository;
	private final MessageSource messageSource;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void publishprojectActivityLog(ProjectActivityLogEvent projectActivityLogEvent) {
		ProjectActivityLog projectActivityLog = ProjectActivityLog.projectActivityLogBuilder()
			.message(messageSource.getMessage(projectActivityLogEvent.getMessage().name(), projectActivityLogEvent.getMessageArgs(), projectActivityLogEvent.getLocale()))
			.project(projectActivityLogEvent.getProject())
			.workspaceUserInfo(projectActivityLogEvent.getWorkspaceUserInfo())
			.build();
		projectActivityLogRepository.save(projectActivityLog);
		log.info("[PROJECT ACTIVITY LOGGING] {}", projectActivityLogEvent.toString());

	}
}
