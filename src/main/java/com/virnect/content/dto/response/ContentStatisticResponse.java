package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description Content Statistic Response Class
 * @since 2020.03.18
 */
@ApiModel
@RequiredArgsConstructor
@Getter
public class ContentStatisticResponse {
    @ApiModelProperty(value = "전체 콘텐츠 갯수", notes = "컨텐츠 서버내에 저장된 전체 콘텐츠의 갯수 입니다.", example = "23")
    private final long totalContents;
    @ApiModelProperty(value = "전체 배포된 콘텐츠 갯수", notes = "컨텐츠 서버내에 저장된 전체 콘텐츠 중 배포된(공정으로 등록된) 갯수입니다.", position = 1, example = "10")
    private final long totalManagedContents;
}
