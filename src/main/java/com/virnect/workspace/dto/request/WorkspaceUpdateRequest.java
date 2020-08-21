package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
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

    @NotBlank(message = "워크스페이스 이름은 최대 29자까지 가능합니다.")
    @Length(max=29)
    private String name;

    private MultipartFile profile;

    @NotBlank(message = "워크스페이스 설명은 최대 39자까지 가능합니다.")
    @Length(max=39)
    private String description;
}
