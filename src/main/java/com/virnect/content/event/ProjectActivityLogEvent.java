package com.virnect.content.event;

import java.util.Locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectActivity;
import com.virnect.content.dto.rest.WorkspaceUserResponse;

@Getter
@RequiredArgsConstructor
public class ProjectActivityLogEvent {
	private final ProjectActivity message;
	private final Object[] messageArgs;
	private final Project project;
	private final WorkspaceUserResponse workspaceUserInfo;
	private final Locale locale;

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
