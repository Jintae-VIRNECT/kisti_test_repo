package com.virnect.content.dto.response;

import com.virnect.content.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ContentInfoListResponse {
    @ApiModelProperty(value = "컨텐츠 정보 배열", notes = "조회된 컨텐츠들의 정보가 담긴 배열입니다.")
    private final List<ContentInfoResponse> contentInfo;
    @ApiModelProperty(value = "페이징 정보", notes = "페이지네이션을 하기 위해 필요한 정보입니다.", position = 1)
    private final PageMetadataResponse pageMeta;
}
