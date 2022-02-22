package com.virnect.workspace.infra.file;

/**
 * Project: PF-Workspace
 * DATE: 2021-09-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum DefaultImageFile {
	WORKSPACE_PROFILE_IMG("workspace-profile.png"),
	WORKSPACE_DEFAULT_LOGO_IMG("virnect-default-logo.png"),
	WORKSPACE_WHITE_LOGO_IMG("virnect-white-logo.png"),
	WORKSPACE_DEFAULT_FAVICON("virnect-default-favicon.ico");

	private final String name;

	DefaultImageFile(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
