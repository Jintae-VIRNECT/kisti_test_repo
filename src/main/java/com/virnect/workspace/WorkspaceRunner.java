package com.virnect.workspace;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.dao.workspace.WorkspaceSettingRepository;
import com.virnect.workspace.domain.workspace.WorkspaceSetting;
import com.virnect.workspace.infra.file.DefaultImageFile;
import com.virnect.workspace.infra.file.FileService;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("onpremise")
@Order(1)
public class WorkspaceRunner implements ApplicationRunner {
	private final WorkspaceSettingRepository workspaceSettingRepository;
	private final FileService fileService;

	@Override
	public void run(ApplicationArguments args) {
		WorkspaceSetting workspaceSetting = workspaceSettingRepository.findFirstByOrderByIdAsc();
		if (workspaceSetting == null) {
			workspaceSetting = WorkspaceSetting.builder()
				.title("VIRNECT")
				.defaultLogo(fileService.getDefaultFileUrl(DefaultImageFile.WORKSPACE_DEFAULT_LOGO_IMG))
				.whiteLogo(fileService.getDefaultFileUrl(DefaultImageFile.WORKSPACE_WHITE_LOGO_IMG))
				.favicon(fileService.getDefaultFileUrl(DefaultImageFile.WORKSPACE_DEFAULT_FAVICON))
				.build();
			workspaceSettingRepository.save(workspaceSetting);
		}
		log.info("[WORKSPACE DEFAULT SETTING] TITLE : [{}], LOGO : [{}], FAVICON : [{}]",
			workspaceSetting.getTitle(),
			workspaceSetting.getDefaultLogo(), workspaceSetting.getFavicon()
		);
	}
}


