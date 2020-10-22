package com.virnect.workspace;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.dao.WorkspaceSettingRepository;
import com.virnect.workspace.domain.WorkspaceSetting;
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
public class WorkspaceRunner implements ApplicationRunner {
	private final WorkspaceSettingRepository workspaceSettingRepository;
	private final FileService fileService;

	@Override
	public void run(ApplicationArguments args) {
		String logoDefault = fileService.getFileUrl("virnect-default-logo.png");
		String logoWhite = fileService.getFileUrl("virnect-white-logo.png");
		String favicon = fileService.getFileUrl("virnect-default-favicon.ico");
		Optional<WorkspaceSetting> workspaceSetting = workspaceSettingRepository.findById(1L);
		if (!workspaceSetting.isPresent()) {
			WorkspaceSetting newWorkspaceSetting = WorkspaceSetting.builder()
				.title("VIRNECT")
				.defaultLogo(logoDefault)
				.greyLogo(logoWhite)
				.favicon(favicon).build();
			workspaceSettingRepository.save(newWorkspaceSetting);
			log.info("[WORKSPACE DEFAULT SETTING] TITLE : [{}], LOGO : [{}], FAVICON : [{}]",
				newWorkspaceSetting.getTitle(),
				newWorkspaceSetting.getDefaultLogo(), newWorkspaceSetting.getFavicon()
			);
			return;
		}
		log.info("[WORKSPACE DEFAULT SETTING] TITLE : [{}], LOGO : [{}], FAVICON : [{}]",
			workspaceSetting.get().getTitle(),
			workspaceSetting.get().getDefaultLogo(), workspaceSetting.get().getFavicon()
		);
	}
}
