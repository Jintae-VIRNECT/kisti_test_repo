package com.virnect.process.dto.rest.response.content;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ProcessManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.18
 */

@Getter
@Setter
@ApiModel
public class ContentStatusInfoResponse {
    @ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;
    @ApiModelProperty(value = "컨텐츠 명", notes = "제작한 컨텐츠의 명칭입니다", position = 1, example = "뻐꾸 파일")
    private String contentName;
    @ApiModelProperty(value = "컨텐츠 상태( PUBLISH(배포 중), MANAGED(공정 관리 중), WAIT(배포 대기 중)", notes = "컨텐츠가 배포중인지, 대기인지, 공정으로 관리되었는지 등의 상태 정보입니다.", position = 2, example = "WAIT")
    private ContentStatus status;
}
