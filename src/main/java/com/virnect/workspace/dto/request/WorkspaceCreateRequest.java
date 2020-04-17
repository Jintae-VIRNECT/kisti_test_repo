package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceCreateRequest {
    @NotNull(message = "워크스페이스를 시작하는 사용자의 uuid는 필수 값입니다.")
    private String userId;
    @NotNull(message = "워크스페이스를 시작하는 사용자의 이름은 필수 값입디나.")
    private String name;
    private MultipartFile profile;
    @NotNull(message = "워크스페이스에 대한 설명은 필수 값입니다.")
    private String description;

    @Override
    public String toString() {
        return "WorkspaceInfo{" +
                "userId='" + userId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
