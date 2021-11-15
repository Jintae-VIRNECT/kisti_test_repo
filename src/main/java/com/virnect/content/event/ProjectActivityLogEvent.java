package com.virnect.content.event;

import java.util.Locale;

import lombok.Getter;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectActivity;
import com.virnect.content.dto.rest.WorkspaceUserResponse;

@Getter
public class ProjectActivityLogEvent {
	private final ProjectActivity message;
	private final Object[] messageArgs;
	private final Project project;
	private final WorkspaceUserResponse workspaceUserInfo;
	private final Locale locale;

	public ProjectActivityLogEvent(
		ProjectActivity message, Object[] messageArgs, Project project,
		WorkspaceUserResponse workspaceUserInfo
	) {
		this.message = message;
		this.messageArgs = messageArgs;
		this.project = project;
		this.workspaceUserInfo = workspaceUserInfo;
		this.locale = Locale.KOREA;
	}

	public ProjectActivityLogEvent(
		ProjectActivity message, Object[] messageArgs, Project project,
		WorkspaceUserResponse workspaceUserInfo,
		Locale locale
	) {
		this.message = message;
		this.messageArgs = messageArgs;
		this.project = project;
		this.workspaceUserInfo = workspaceUserInfo;
		this.locale = locale;
	}

	@Override
	public String toString() {
		return "ProjectActivityLogEvent{" +
			"message=" + message +
			", project=" + project +
			", workspaceUserInfo=" + workspaceUserInfo +
			", locale=" + locale.toString() +
			'}';
	}
}
