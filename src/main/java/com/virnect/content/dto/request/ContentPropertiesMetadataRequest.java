package com.virnect.content.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.21
 */
@Getter
@Setter
@ToString
public class ContentPropertiesMetadataRequest {
    @NotBlank
    private String properties;

    @NotBlank
    private String userUUID;

    @Builder
    public ContentPropertiesMetadataRequest(@NotBlank String properties, @NotBlank String userUUID) {
        this.properties = properties;
        this.userUUID = userUUID;
    }
}
