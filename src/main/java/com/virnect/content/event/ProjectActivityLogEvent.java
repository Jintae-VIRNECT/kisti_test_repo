package com.virnect.content.event;

import java.util.Locale;

import lombok.Getter;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectActivity;
import com.virnect.content.dto.rest.WorkspaceUserResponse;

@Getter
public class ProjectActivityLogEvent {
	private final ProjectActivity activity;
	private final String activityProperty;
	private final Project project;
	private final WorkspaceUserResponse workspaceUserInfo;

	public ProjectActivityLogEvent(
		ProjectActivity message, String activityProperty, Project project,
		WorkspaceUserResponse workspaceUserInfo
	) {
		this.activity = message;
		this.activityProperty = activityProperty;
		this.project = project;
		this.workspaceUserInfo = workspaceUserInfo;
	}

	@Override
	public String toString() {
		return "ProjectActivityLogEvent{" +
			"activity=" + activity +
			", activityProperty='" + activityProperty + '\'' +
			", project=" + project +
			", workspaceUserInfo=" + workspaceUserInfo +
			'}';
	}
}
