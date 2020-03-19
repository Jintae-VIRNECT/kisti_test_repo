package com.virnect.content.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Upload Request DTO Class
 */


@Getter
@Setter
public class ContentUploadRequest {
    @NotNull
    private MultipartFile content;

    @NotBlank
    private String contentUUID;

    @NotBlank
    private String name;

    @NotBlank
    private String metadata;

    @NotBlank
    private String userUUID;

    @NotNull
    @PositiveOrZero
    private int aruco;

    @Override
    public String toString() {
        return "ContentUploadRequest{" +
                "content=" + content.getOriginalFilename() +
                ", contentUUID='" + contentUUID + '\'' +
                ", name='" + name + '\'' +
                ", metadata='" + metadata + '\'' +
                ", userUUID='" + userUUID + '\'' +
                ", aruco=" + aruco +
                '}';
    }
}
