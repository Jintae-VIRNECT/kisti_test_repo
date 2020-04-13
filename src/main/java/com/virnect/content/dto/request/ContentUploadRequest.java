package com.virnect.content.dto.request;

import com.virnect.content.domain.Types;
import com.virnect.content.domain.TargetType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Upload Request DTO Class
 */


@Getter
@Setter
@ToString
public class ContentUploadRequest {
    @NotBlank
    private String workspaceUUID;

    @NotNull
    private MultipartFile content;

    private Types contentType;

    @NotBlank
    private String name;

    @NotBlank
    private String metadata;

    @NotBlank
    private String userUUID;

    @NotNull
    private TargetType targetType;
}
