package com.virnect.workspace;

import com.virnect.workspace.dao.workspace.WorkspaceSettingRepository;
import com.virnect.workspace.domain.workspace.WorkspaceSetting;
import com.virnect.workspace.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

	/**
	 * 워크스페이스 메타데이터 체크(파비콘, 로고, 프로필)
	 * @param args
	 */
	@Override
	public void run(ApplicationArguments args) {
        String workspaceProfile = fileService.getFileUrl("workspace-profile.png");
        String logoDefault = fileService.getFileUrl("virnect-default-logo.png");
		String logoWhite = fileService.getFileUrl("virnect-white-logo.png");
		String favicon = fileService.getFileUrl("virnect-default-favicon.ico");
		List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
		if (workspaceSettingList.isEmpty()) {
			WorkspaceSetting newWorkspaceSetting = WorkspaceSetting.builder()
				.title("VIRNECT")
				.defaultLogo(logoDefault)
				.whiteLogo(logoWhite)
				.favicon(favicon).build();
			workspaceSettingRepository.save(newWorkspaceSetting);
			log.info("[WORKSPACE DEFAULT SETTING] TITLE : [{}], LOGO : [{}], FAVICON : [{}]",
				newWorkspaceSetting.getTitle(),
				newWorkspaceSetting.getDefaultLogo(), newWorkspaceSetting.getFavicon()
			);
			return;
		}
		Optional<WorkspaceSetting> workspaceSetting = workspaceSettingList.stream().findFirst();
		log.info("[WORKSPACE DEFAULT SETTING] TITLE : [{}], LOGO : [{}], FAVICON : [{}]",
			workspaceSetting.get().getTitle(),
			workspaceSetting.get().getDefaultLogo(), workspaceSetting.get().getFavicon()
		);
	}
}


