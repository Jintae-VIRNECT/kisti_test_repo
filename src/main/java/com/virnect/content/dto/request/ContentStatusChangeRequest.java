package com.virnect.content.dto.request;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.18
 */
@Getter
@Setter
@ToString
public class ContentStatusChangeRequest {
    @NotNull
    private String contentUUID;

    @NotBlank
    private String status;

    @NotBlank
    private String userUUID;
}
