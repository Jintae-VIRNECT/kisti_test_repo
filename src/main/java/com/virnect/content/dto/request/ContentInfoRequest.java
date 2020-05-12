package com.virnect.content.dto.request;

import com.virnect.content.domain.Types;
import com.virnect.content.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    @ApiModelProperty(value="컨텐츠 종류", notes="AUGMENTED_REALITY(default), ASSISTED_REALITY, CROCESS_PLATFORM, MIXED_REALITY", example="AUGMENTED_REALITY")
    Types contentType;

    @ApiModelProperty(value="공유여부", notes="YES, NO", example="NO")
    YesOrNo shared;

    @NotBlank
    @NotNull
    @ApiModelProperty(value="요청 사용자의 고유번호", notes="YES, NO", example="4250001664a1b08486700a10a90a1af1")
    String userUUID;
}
