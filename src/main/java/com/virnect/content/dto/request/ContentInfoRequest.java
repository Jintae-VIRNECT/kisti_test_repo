package com.virnect.content.dto.request;

import com.virnect.content.domain.Types;
import com.virnect.content.domain.YesOrNo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.12
 */
@Getter
@Setter
public class ContentInfoRequest {
    Types contentType;

    YesOrNo shared;

    @NotBlank
    String userUUID;
}
