package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceUpdateRequest {
    @NotBlank
    private String workspaceId;
    @NotBlank
    private String userId;
    @NotBlank
    private String name;

    private MultipartFile profile;

    @NotBlank
    private String description;
}
