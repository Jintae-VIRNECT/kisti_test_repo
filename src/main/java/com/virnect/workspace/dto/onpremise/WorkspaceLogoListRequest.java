package com.virnect.workspace.dto.onpremise;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceLogoListRequest {
	private MultipartFile default_;
	private MultipartFile grey;
	private MultipartFile white;
}
