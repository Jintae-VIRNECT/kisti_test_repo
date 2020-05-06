package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
    private String userId;
    private String name;
    private MultipartFile profile;
    private String description;

    @Override
    public String toString() {
        return "WorkspaceInfo{" +
                "userId='" + userId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
