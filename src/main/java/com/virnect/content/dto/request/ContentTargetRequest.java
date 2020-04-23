package com.virnect.content.dto.request;

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
 * DESCRIPTION: Content update Request DTO Class
 */

@Getter
@Setter
@ToString
public class ContentTargetRequest {

    @NotBlank
    private String targetData;

    @NotBlank
    private String targetType;

    @NotBlank
    private String userUUID;
}
