package com.virnect.content.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content update Request DTO Class
 */

@Getter
@Setter
public class ContentUpdateRequest {
    @NotNull
    private MultipartFile content;

    @NotBlank
    private String name;

    @NotBlank
    private String metadata;

    @NotBlank
    private String userUUID;
}
